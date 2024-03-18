CREATE TABLE app_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL
);

CREATE TABLE project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    app_user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    ai_model_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_project_app_user_id FOREIGN KEY (app_user_id) REFERENCES app_user (id)
);

CREATE TABLE image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    old_name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    data BLOB NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_image_project_id FOREIGN KEY (project_id) REFERENCES project (id)
);

CREATE TABLE result (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   image_id BIGINT NOT NULL,
   text_recognition_image_data BLOB NOT NULL,
   elements CLOB NOT NULL,
   created_at TIMESTAMP NOT NULL,
   updated_at TIMESTAMP NOT NULL,
   CONSTRAINT fk_result_image_id FOREIGN KEY (image_id) REFERENCES image (id)
);
