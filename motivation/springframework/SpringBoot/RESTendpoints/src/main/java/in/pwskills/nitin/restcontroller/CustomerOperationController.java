package in.pwskills.nitin.restcontroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/customer")
public class CustomerOperationController {

    // General report endpoint
    @GetMapping("/report")
    public ResponseEntity<String> showCustomerReport() {
        return new ResponseEntity<String>("FROM GET-ShowReport Method", HttpStatus.OK);
    }

    // Query parameter-based report endpoint
    @GetMapping("/report/query")
    public String reportData(
            @RequestParam("cid") Integer cno,
            @RequestParam String cname) {
        return "Query Params: " + cno + " " + cname;
    }

    // Path variable-based report endpoint
    @GetMapping("/report/{id}/{cname}")
    public String fetchData(@PathVariable("id") Integer cno, @PathVariable String cname) {
        return "Path Variables: " + cno + " " + cname;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerCustomer() {
        return new ResponseEntity<String>("FROM POST-RegisterCustomer Method", HttpStatus.OK);
    }

    @PutMapping("/modify")
    public ResponseEntity<String> updateCustomer() {
        return new ResponseEntity<String>("FROM Put-UpdateCustomer Method", HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCustomer() {
        return new ResponseEntity<String>("FROM Delete-deleteCustomer Method", HttpStatus.OK);
    }
}