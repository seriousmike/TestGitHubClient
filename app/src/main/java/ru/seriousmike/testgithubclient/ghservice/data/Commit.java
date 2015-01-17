package ru.seriousmike.testgithubclient.ghservice.data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SeriousM on 17.01.2015.
 */
public class Commit {
    public String url;
    public String sha;
    public CommitInner commit;

    public static class CommitInner {
        public String message;
        public Author author;
    }

    public static class Author {
        public String name;
        public String email;
        public Date date;
    }

    public String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        return dateFormat.format(commit.author.date);
    }

    public String toString() {

        return getName()+" "+commit.author.name+" "+getDate()+" => "+commit.message;
//        return getName();
    }

    public String getName() {
        return sha.substring(0,7);
    }

}