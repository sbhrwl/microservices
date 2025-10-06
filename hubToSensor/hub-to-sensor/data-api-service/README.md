# Data API service
| API Name	| Endpoint	| Return Type (DTO)	| Implementation Focus (Java/Spring Boot)|
| --------- | --------- | ----------------- | -------------------------------------- | 
| API 1: Request Details	| GET /requests/{id}	| ControlRequestDTO	| ControlRequest Entity -> Service maps to ControlRequestDTO.| 
| API 2: Request Status Details	| GET /requests/{id}/logs	| List<ChangeLogDTO>	| RequestChangeLog Entity -> Service fetches logs -> maps to List<ChangeLogDTO>.| 
| API 3: Request Tracker	| GET /requests/{id}/tracker	| RequestTrackerDTO	| JPA @OneToMany Fetch -> Service combines/maps to RequestTrackerDTO.| 
| Error Handling	| All endpoints	| HTTP 404 + JSON Error Body	| Global @ControllerAdvice to catch ResourceNotFoundException.| 
