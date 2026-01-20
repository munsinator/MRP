package at.fh.model;

import java.util.UUID;

public class Genre {
    private UUID id;
    private String name;

    private Genre() {}

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static class Builder{
        private final Genre genre = new Genre();

        public Builder id(UUID id){
            genre.id = id;
            return this;
        }
        public Builder name(String name){
            genre.name = name;
            return this;
        }

        public Genre build(){
            return genre;
        }
    }
}
