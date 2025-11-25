package artifact.GUI;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.format.DateTimeFormatter;
import java.util.Base64;

import artifact.Backend.Controller.BaseController;
import artifact.Backend.Models.SupportMessage;
import artifact.Backend.Models.SupportTicket;
import artifact.Backend.Tags.TicketStatus;
import artifact.Backend.Models.User;
import artifact.Backend.Services.Impl.SupportService;
import artifact.Backend.Tags.UserRole;

/**
 * A modern, minimal chat interface for Support Tickets.
 */
public class TicketConversationView extends BorderPane {

    private final SupportTicket ticket;
    private final User currentUser;
    private final SupportService supportService;
    private final VBox messageContainer = new VBox(15);
    private final TextArea replyField = new TextArea();
    private final ScrollPane scrollPane;
    private final Runnable onUpdateCallback;

    // Modern Colors
    private static final String PRIMARY_COLOR = "#00A4BF";
    private static final String BG_COLOR = "#F4F6F8";
    private static final String TEXT_HEADER = "#1A202C";
    private static final String TEXT_BODY = "#4A5568";
    private static final String TEXT_LIGHT = "#94A3B8";

    public TicketConversationView(SupportTicket ticket, BaseController controller, Runnable onUpdateCallback) {
        this.ticket = ticket;
        this.currentUser = controller.getUserSession().getCurrentUser();
        this.supportService = new SupportService(); 
        this.onUpdateCallback = onUpdateCallback;

        // Inject CSS
        getStylesheets().add(getInlineStyleSheet());
        setStyle("-fx-background-color: " + BG_COLOR + ";");

        // --- 1. Header ---
        setTop(createHeader());

        // --- 2. Messages Area ---
        messageContainer.setPadding(new Insets(20, 25, 20, 25));
        messageContainer.setFillWidth(true); 
        
        scrollPane = new ScrollPane(messageContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        
        setCenter(scrollPane);

        // Render existing messages
        refreshMessages();

        // --- 3. Input Area ---
        setBottom(createInputArea());
        
        // Auto-scroll to bottom on load
        Platform.runLater(() -> {
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        });
    }

    public TicketConversationView(SupportTicket ticket, BaseController controller) {
        this(ticket, controller, () -> {});
    }

    private VBox createHeader() {
        VBox headerContainer = new VBox(0);
        headerContainer.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        
        HBox headerContent = new HBox(15);
        headerContent.setPadding(new Insets(15, 25, 15, 25));
        headerContent.setAlignment(Pos.CENTER_LEFT);
        
        // Ticket Icon / Avatar
        StackPane icon = new StackPane();
        Circle bg = new Circle(20, Color.web("#E0F7FA"));
        Label iconLabel = new Label("#" + ticket.id());
        iconLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        iconLabel.setTextFill(Color.web(PRIMARY_COLOR));
        icon.getChildren().addAll(bg, iconLabel);
        
        // Ticket Info
        VBox infoBox = new VBox(2);
        Label subject = new Label(ticket.subject());
        subject.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        subject.setTextFill(Color.web(TEXT_HEADER));
        
        HBox statusRow = new HBox(8);
        statusRow.setAlignment(Pos.CENTER_LEFT);
        Label statusBadge = createStatusBadge(ticket.status());
        statusRow.getChildren().add(statusBadge);
        
        infoBox.getChildren().addAll(subject, statusRow);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Actions
        HBox actions = new HBox(10);
        if (currentUser.role() == UserRole.STAFF && ticket.status() != TicketStatus.RESOLVED) {
            Button resolveBtn = new Button("Mark Resolved");
            resolveBtn.getStyleClass().add("resolve-button");
            resolveBtn.setOnAction(e -> handleResolve());
            actions.getChildren().add(resolveBtn);
        }
        
        headerContent.getChildren().addAll(icon, infoBox, spacer, actions);
        headerContainer.getChildren().add(headerContent);
        
        return headerContainer;
    }

    private Label createStatusBadge(TicketStatus status) {
        Label badge = new Label(status.toString());
        badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        badge.setPadding(new Insets(3, 8, 3, 8));
        
        String style = "-fx-background-radius: 10px; -fx-text-fill: white; ";
        switch(status) {
            case OPEN: style += "-fx-background-color: #2ECC71;"; break; // Green
            case IN_PROGRESS: style += "-fx-background-color: " + PRIMARY_COLOR + ";"; break; // Teal
            case RESOLVED: style += "-fx-background-color: #94A3B8;"; break; // Grey
            default: style += "-fx-background-color: #F1C40F;"; break; // Yellow
        }
        badge.setStyle(style);
        return badge;
    }

    private HBox createInputArea() {
        HBox container = new HBox(15);
        container.setPadding(new Insets(15, 25, 15, 25));
        container.setStyle("-fx-background-color: white; -fx-border-color: #F1F5F9; -fx-border-width: 1 0 0 0;");
        container.setAlignment(Pos.CENTER);

        // Input Wrapper
        replyField.setPromptText("Type your message...");
        replyField.setPrefHeight(45);
        replyField.setPrefRowCount(1);
        replyField.setWrapText(true);
        replyField.getStyleClass().add("chat-input");
        HBox.setHgrow(replyField, Priority.ALWAYS);

        Button sendBtn = new Button("Send");
        sendBtn.setPrefHeight(40);
        sendBtn.setPrefWidth(80);
        sendBtn.getStyleClass().add("send-button");
        sendBtn.setOnAction(e -> handleSend());
        
        // Send on Enter
        replyField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER && !event.isShiftDown()) {
                event.consume();
                handleSend();
            }
        });

        container.getChildren().addAll(replyField, sendBtn);
        return container;
    }

    private void refreshMessages() {
        messageContainer.getChildren().clear();
        
        SupportTicket freshTicket = supportService.findById(ticket.id());
        if (freshTicket == null) return;

        for (SupportMessage msg : freshTicket.messages()) {
            messageContainer.getChildren().add(createMessageBubble(msg));
        }
        
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    private HBox createMessageBubble(SupportMessage msg) {
        HBox row = new HBox();
        boolean isMe = msg.senderRole() == currentUser.role();
        
        // Avatar Circle
        StackPane avatar = createAvatar(msg.senderName(), isMe);
        
        // Bubble Layout
        VBox bubble = new VBox(2);
        bubble.setMaxWidth(450);
        bubble.setPadding(new Insets(12, 16, 12, 16));
        
        // Message Text
        Text text = new Text(msg.message());
        text.setFont(Font.font("Segoe UI", 14));
        text.wrappingWidthProperty().bind(bubble.widthProperty().subtract(32));

        // Metadata (Name + Time)
        Label meta = new Label();
        meta.setFont(Font.font("Segoe UI", 10));
        String timeStr = msg.timestamp().format(DateTimeFormatter.ofPattern("HH:mm"));
        
        // Styling logic
        if (isMe) {
            row.setAlignment(Pos.CENTER_RIGHT);
            
            // Me: Solid Primary Color
            bubble.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-background-radius: 18 18 0 18;");
            text.setFill(Color.WHITE);
            meta.setText(timeStr);
            meta.setTextFill(Color.web("#E0F7FA")); // Light Teal for contrast
            meta.setAlignment(Pos.CENTER_RIGHT);
            
            // Align: Message + Avatar
            HBox content = new HBox(10);
            content.setAlignment(Pos.BOTTOM_RIGHT);
            content.getChildren().addAll(bubble, avatar);
            row.getChildren().add(content);
            
        } else {
            row.setAlignment(Pos.CENTER_LEFT);
            
            // Them: White Card
            bubble.setStyle("-fx-background-color: white; -fx-background-radius: 18 18 18 0; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 2, 0, 0, 1);");
            text.setFill(Color.web(TEXT_HEADER));
            
            meta.setText(msg.senderName() + " • " + timeStr);
            meta.setTextFill(Color.web(TEXT_LIGHT));
            
            // Align: Avatar + Message
            HBox content = new HBox(10);
            content.setAlignment(Pos.BOTTOM_LEFT);
            content.getChildren().addAll(avatar, bubble);
            row.getChildren().add(content);
        }

        bubble.getChildren().addAll(text, meta);
        return row;
    }
    
    private StackPane createAvatar(String name, boolean isMe) {
        StackPane stack = new StackPane();
        Circle c = new Circle(16);
        
        String initial = name != null && !name.isEmpty() ? name.substring(0, 1).toUpperCase() : "?";
        Label l = new Label(initial);
        l.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        
        if (isMe) {
            c.setFill(Color.web(PRIMARY_COLOR).deriveColor(0, 1, 1, 0.2)); // Faint primary
            l.setTextFill(Color.web(PRIMARY_COLOR));
        } else {
            c.setFill(Color.WHITE);
            c.setStroke(Color.web("#E2E8F0"));
            l.setTextFill(Color.web(TEXT_BODY));
        }
        
        stack.getChildren().addAll(c, l);
        return stack;
    }

    private void handleResolve() {
        supportService.resolveTicket(ticket.id());
        if (onUpdateCallback != null) onUpdateCallback.run();
        if (getScene() != null && getScene().getWindow() instanceof javafx.stage.Stage) {
            ((javafx.stage.Stage) getScene().getWindow()).close();
        }
    }

    private void handleSend() {
        String text = replyField.getText().trim();
        if (text.isEmpty()) return;
        supportService.sendMessage(ticket.id(), currentUser, text);
        replyField.clear();
        refreshMessages();
    }
    
    private String getInlineStyleSheet() {
        String css = 
            // Input Field
            ".chat-input {" +
            "    -fx-background-color: #F8FAFC;" +
            "    -fx-border-color: #E2E8F0;" +
            "    -fx-border-radius: 20px;" +
            "    -fx-background-radius: 20px;" +
            "    -fx-padding: 8 15;" +
            "    -fx-text-fill: " + TEXT_HEADER + ";" +
            "    -fx-font-family: 'Segoe UI';" +
            "    -fx-font-size: 14px;" +
            "}" +
            ".chat-input:focused {" +
            "    -fx-background-color: white;" +
            "    -fx-border-color: " + PRIMARY_COLOR + ";" +
            "}" +
            
            // Send Button
            ".send-button {" +
            "    -fx-background-color: " + PRIMARY_COLOR + ";" +
            "    -fx-text-fill: white;" +
            "    -fx-font-weight: bold;" +
            "    -fx-background-radius: 20px;" +
            "    -fx-cursor: hand;" +
            "}" +
            ".send-button:hover {" +
            "    -fx-background-color: #008C9E;" +
            "}" +
            
            // Resolve Button
            ".resolve-button {" +
            "    -fx-background-color: white;" +
            "    -fx-text-fill: #10B981;" + // Green text
            "    -fx-border-color: #10B981;" +
            "    -fx-border-radius: 4px;" +
            "    -fx-background-radius: 4px;" +
            "    -fx-font-weight: bold;" +
            "    -fx-font-size: 11px;" +
            "    -fx-cursor: hand;" +
            "}" +
            ".resolve-button:hover {" +
            "    -fx-background-color: #10B981;" +
            "    -fx-text-fill: white;" +
            "}" +
            
            // ScrollPane Clean-up
            ".scroll-pane > .viewport {" +
            "    -fx-background-color: transparent;" +
            "}" +
            ".scroll-bar:vertical {" +
            "    -fx-background-color: transparent;" +
            "}" +
            ".scroll-bar:vertical .thumb {" +
            "    -fx-background-color: #CBD5E1;" +
            "    -fx-background-radius: 5em;" +
            "}";

        return "data:text/css;base64," + Base64.getEncoder().encodeToString(css.getBytes());
    }
}