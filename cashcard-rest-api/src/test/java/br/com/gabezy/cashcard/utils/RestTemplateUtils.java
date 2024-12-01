package br.com.gabezy.cashcard.utils;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RestTemplateUtils {

    private static final String LOCALHOST = "http://localhost";

    public <T> ResponseEntity<T> buildRequestRest(Class<T> type, TestRestTemplate testRestTemplate,
                                                  RestTemplateCashCardFilter filter)  {
        String url = String.format("%s:%d/%s", LOCALHOST, filter.getPort(), filter.getEndpoint());

        TestRestTemplate template = testRestTemplate.withBasicAuth(filter.getUsername(), filter.getPassword());

        try {
            if (filter.isPost()) {
                return template.postForEntity(url, filter.getBody(), type);
            }

            if (filter.isPut()) {
                HttpEntity<Object> entity = new HttpEntity<>(filter.getBody());
                return template.exchange(url, HttpMethod.PUT, entity, type);
            }

            if (filter.isDelete()) {
                return template.exchange(url, HttpMethod.DELETE, null, type);
            }

            return template.getForEntity(url, type);
        } catch (Exception e) {
            throw new RuntimeException("Error while doing the REST request", e);
        }

    }

    public static class RestTemplateCashCardFilter {

        private final String endpoint;

        private final String username;

        private final String password;

        private final Object body;

        private final int port;

        private final boolean post;

        private final boolean put;

        private final boolean delete;

        public RestTemplateCashCardFilter(Builder builder) {
            this.endpoint = builder.endpoint;
            this.username = builder.username;
            this.password = builder.password;
            this.port = builder.port;
            this.body = builder.body;
            this.post = builder.post;
            this.put = builder.put;
            this.delete = builder.delete;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public int getPort() {
            return port;
        }

        public Object getBody() {
            return body;
        }

        public boolean isPost() {
            return post;
        }

        public boolean isPut() {
            return put;
        }

        public boolean isDelete() {
            return delete;
        }

        public static class Builder {

            private String  endpoint;
            private String  username;
            private String  password;
            private int     port;
            private Object  body;
            private boolean post;
            private boolean put;
            private boolean delete;

            public Builder endpoint(String endpoint) {
                this.endpoint = endpoint;
                return this;
            }

            public Builder username(String username) {
                this.username = username;
                return this;
            }

            public Builder password(String password) {
                this.password = password;
                return this;
            }

            public Builder port(int port) {
                this.port = port;
                return this;
            }

            public Builder body(Object body) {
                this.body = body;
                return this;
            }

            public Builder post(boolean post) {
                this.post = post;
                return this;
            }

            public Builder put(boolean put) {
                this.put = put;
                return this;
            }

            public Builder delete(boolean delete) {
                this.delete = delete;
                return this;
            }

            public RestTemplateCashCardFilter build() {
                return new RestTemplateCashCardFilter(this);
            }

        }

    }


}
