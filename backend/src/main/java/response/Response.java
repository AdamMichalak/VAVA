package response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;


@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response<T> {

    private Integer status;
    private T payload;
    private Object errors;
    private Object metadata;

    public static <T> Response<T> badRequest() {
        Response<T> response = new Response<>();
        response.setStatus(400);
        return response;
    }

    public static <T> Response<T> ok() {
        Response<T> response = new Response<>();
        response.setStatus(200);
        return response;
    }

//    public static <T> Response<T> unauthorized() {
//        Response<T> response = new Response<>();
//        response.setStatus(Status.UNAUTHORIZED);
//        return response;
//    }
//
//    public static <T> Response<T> validationException() {
//        Response<T> response = new Response<>();
//        response.setStatus(Status.VALIDATION_EXCEPTION);
//        return response;
//    }
//
    public static <T> Response<T> wrongCredentials() {
        Response<T> response = new Response<>();
        response.setStatus(401);
        return response;
    }
//
//    public static <T> Response<T> accessDenied() {
//        Response<T> response = new Response<>();
//        response.setStatus(Status.ACCESS_DENIED);
//        return response;
//    }
//
    public static <T> Response<T> exception() {
        Response<T> response = new Response<>();
        response.setStatus(500);
        return response;
    }
//
//    public static <T> Response<T> notFound() {
//        Response<T> response = new Response<>();
//        response.setStatus(Status.NOT_FOUND);
//        return response;
//    }
//
//    public static <T> Response<T> duplicateEntity() {
//        Response<T> response = new Response<>();
//        response.setStatus(Status.DUPLICATE_ENTITY);
//        return response;
//    }
//
//    public void addErrorMsgToResponse(String errorMsg, Exception ex) {
//        ResponseError error = new ResponseError()
//                .setDetails(errorMsg)
//                .setMessage(ex.getMessage())
//                .setTimestamp(new Date());
//        setErrors(error);
//    }



    @Getter
    @Accessors(chain = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PageMetadata {
        private final int size;
        private final long totalElements;
        private final int totalPages;
        private final int number;

        public PageMetadata(int size, long totalElements, int totalPages, int number) {
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.number = number;
        }
    }

}

