CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE genre (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE media_entry (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    release_year INT,
    age_restriction INT
);

CREATE TABLE media_genre (
    media_id UUID NOT NULL,
    genre_id UUID NOT NULL,
    PRIMARY KEY (media_id, genre_id),
    FOREIGN KEY (media_id) REFERENCES media_entry(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genre(id) ON DELETE CASCADE
);

CREATE TABLE rating (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_by UUID NOT NULL,
    media_id UUID NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT FALSE,
    stars INT CHECK (stars BETWEEN 1 AND 5) NOT NULL,
    comment VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (media_id) REFERENCES media_entry(id) ON DELETE CASCADE,
    UNIQUE (created_by, media_id)
);

CREATE TABLE rating_like (
    user_id UUID NOT NULL,
    rating_id UUID NOT NULL,
    PRIMARY KEY (user_id, rating_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (rating_id) REFERENCES rating(id) ON DELETE CASCADE
);

CREATE TABLE media_favorite (
    user_id UUID NOT NULL,
    media_id UUID NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (media_id) REFERENCES media_entry(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, media_id)
);

INSERT INTO users (id, username, password_hash) VALUES
                                     ('11111111-1111-1111-1111-111111111111', 'system_user', 'password');
