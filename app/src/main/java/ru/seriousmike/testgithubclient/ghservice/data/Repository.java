package ru.seriousmike.testgithubclient.ghservice.data;

import java.util.Date;

/**
 * Created by SeriousM on 14.01.2015.
 */
public class Repository {
    public long id;
    public Owner owner;
    public String name;
    public String full_name;
    public String description;
    public boolean fork;
    public String url;
    public String html_url;
    public int forks_count;
    public int watchers_count;

    public Date created_at;
    public Date pushed_at;

    public static class Owner {
        public String login;
        public long id;
        public String avatar_url;
        public String url;
        public String html_url;
        public String type;
    }

    @Override
    public String toString() {
        return "#"+id+" "+full_name+" ("+created_at.toString()+"/"+pushed_at+")";
    }

}

