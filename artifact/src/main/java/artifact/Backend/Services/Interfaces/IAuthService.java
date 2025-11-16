package artifact.Backend.Services.Interfaces;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Models.User;
import artifact.Backend.Models.DTO.LoginRequest;
import artifact.Backend.Models.DTO.RegisterRequest;


public interface IAuthService 
{
    public ServiceResult login(LoginRequest request);
    public ServiceResult initiateRegistration(RegisterRequest request);
    public boolean verifyAndRegister(String email, String enteredOtp);
    public User loginWithGoogle(String googleEmail, String googleName);

}

