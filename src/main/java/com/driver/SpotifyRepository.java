package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user=new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist=new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        boolean b=false;
        for(Artist a:artists){
            if(a.getName().equals(artistName)){ b=true; break;}
        }
        if(!b) createArtist(artistName);
        Album album=new Album(title);
        albums.add(album);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        boolean b=false;
        Album album=new Album();
        for(Album a:albums){
            if(a.getTitle().equals(albumName)){ album=a;b=true; break;}
        }
        if(!b) throw new Exception("Album does not exist");
        Song song=new Song(title,length);
        songs.add(song);
        if(albumSongMap.containsKey(album))
            albumSongMap.get(album).add(song);
        else{
            List<Song> list=new ArrayList<>();
            list.add(song);
            albumSongMap.put(album,list);
        }
        return song;
    }


    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user=new User();
        boolean b=false;
        for(User u:users){
            if(u.getMobile().equals(mobile)) {b=true;user=u; break;}
        }
        if(!b) throw new Exception("User does not exist");
        Playlist playlist=new Playlist(title);
        playlists.add(playlist);
        List<User> list2=new ArrayList<>();
        list2.add(user);
        playlistListenerMap.put(playlist,list2);
        for(Song song:songs){
            if(song.getLength()==length){
                if(playlistSongMap.containsKey(playlist)) {
                    playlistSongMap.get(playlist).add(song);
                }
                else {
                    List<Song> list1=new ArrayList<Song>();
                    list1.add(song);
                    playlistSongMap.put(playlist,list1);
                }
                creatorPlaylistMap.put(user,playlist);
                if(userPlaylistMap.containsKey(user))
                    userPlaylistMap.get(user).add(playlist);
                else{
                    List<Playlist> list3=new ArrayList<>();
                    list3.add(playlist);
                    userPlaylistMap.put(user,list3);
                }
            }
        }
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user=new User();
        boolean b=false;
        for(User u:users){
            if(u.getMobile().equals(mobile)) {b=true;user=u; break;}
        }
        if(!b) throw new Exception("User does not exist");
        Playlist playlist=new Playlist(title);
        playlists.add(playlist);
        List<User> list2=new ArrayList<>();
        list2.add(user);
        creatorPlaylistMap.put(user,playlist);
        playlistListenerMap.put(playlist,list2);
        for(Song song:songs){
            if(songTitles.contains(song.getTitle())){
                if(playlistSongMap.containsKey(playlist)) playlistSongMap.get(playlist).add(song);
                else {
                    List<Song> list=new ArrayList<Song>();
                    list.add(song);
                    playlistSongMap.put(playlist,list);
                }
                if(userPlaylistMap.containsKey(user)) userPlaylistMap.get(user).add(playlist);
                else{
                    List<Playlist> list3=new ArrayList<>();
                    list3.add(playlist);
                    userPlaylistMap.put(user,list3);
                }
            }
        }
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user=null;
        for(User u:users){
            if(u.getMobile().equals(mobile)) {
                user = u;
                break;
            }
        }
        if(user==null) throw new Exception("User does not exist");
        Playlist playlist=null;
        for(Playlist p:playlists){
            if(p.getTitle().equals(playlistTitle)){
                playlist=p;
                break;
            }
        }
        if(playlist==null) throw new Exception("Playlist does not exist");
        if(!playlistListenerMap.get(playlist).contains(user))
            playlistListenerMap.get(playlist).add(user);
        if(!userPlaylistMap.get(user).contains(playlist))
            userPlaylistMap.get(user).add(playlist);
        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user=null;
        for(User u:users){
            if(u.getMobile().equals(mobile)){
                user=u;
                break;
            }
        }
        if(user==null) throw new Exception("User does not exist");
        Song s=null;
        for(Song song:songs){
            if(song.getTitle().equals(songTitle)){
                s=song;
                if(songLikeMap.containsKey(s)){
                    if(!songLikeMap.get(s).contains(user)) {
                        s.setLikes(s.getLikes()+1);
                        songLikeMap.get(s).add(user);
                    }
                }
                else{
                    List<User> userList=new ArrayList<>();
                    userList.add(user);
                    songLikeMap.put(song,userList);
                    if(!songLikeMap.get(song).contains(user)) {
                        s.setLikes(s.getLikes() + 1);
                        songLikeMap.get(song).add(user);
                    }
                }
                //Getting Album of the Song
                Album a=null;
                for(Album album:albumSongMap.keySet()){
                    if(albumSongMap.get(album).contains(s)){
                        a=album;
                        break;
                    }
                }
                //Getting Artist of the Song
                Artist art=null;
                for(Artist artist:artistAlbumMap.keySet()){
                    if(artistAlbumMap.get(artist).contains(a)){
                        art=artist;
                        break;
                    }
                }
                art.setLikes(art.getLikes()+1);
            }
        }
        if(s==null) throw new Exception("Song does not exist");
        return s;
    }

    public String mostPopularArtist() {
        Artist artist=new Artist();
        int max=Integer.MIN_VALUE;
        for(Artist a:artists){
            if(a.getLikes()>max){
                max=a.getLikes();
                artist=a;
            }
        }
        return artist.getName();
    }

    public String mostPopularSong() {
        Song song=new Song();
        int max=Integer.MIN_VALUE;
        for(Song s:songs){
            if(s.getLikes()>max){
                max=s.getLikes();
                song=s;
            }
        }
        return song.getTitle();
    }
}
