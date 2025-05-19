package in.pwskills.nitin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootRestApi01Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRestApi01Application.class, args);
	}
}

// Add context-path
// server.servlet.context-path=/JsonToJavaObject
// request : POST -> http://localhost:9999/JsonToJavaObject/customer/register
// {
// "cid":7,
// "cname":"dhoni",
// "compDetails":[
// {"name": "iNeuron","location":"BGLR","size":250},
// {"name": "pwskills","location":"Noida","size":350},
// {"name": "IGATE","location":"HYD","size":300}
// ],
// "dob":"1991-01-03",
// "purchaseDate":"2022-06-05 19:01:23",
// "familyDetails":[
// {"adharNo": 123456,"pan":123456},
// {"adharNo": 234234,"pan":4443335}
// ]
// }
// response
// Customer [
// cid=7, cname=dhoni,
// compDetails=[
// Company [name=iNeuron, location=BGLR, size=250],
// Company [name=pwskills, location=Noida, size=350],
// Company [name=IGATE, location=HYD, size=300]
// ],
// dob=1991-01-03,
// purchaseDate=2022-06-05T19:01:23,
// familyDetails=[
// {adharNo=123456, pan=123456},
// {adharNo=234234, pan=4443335}
// ]
// ]
