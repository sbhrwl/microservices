import com.example.postservice.model.CommandRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommandController {

    @PostMapping("/commands")
    public ResponseEntity<String> postCommand(@RequestBody CommandRequest commandRequest) {
        // Process the command request here
        // For now, we will just return a success message
        return new ResponseEntity<>("Command task created and sent for processing.", HttpStatus.ACCEPTED);
    }
}