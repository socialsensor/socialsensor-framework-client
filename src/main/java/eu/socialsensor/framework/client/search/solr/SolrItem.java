package eu.socialsensor.framework.client.search.solr;

import com.google.gson.Gson;
import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.domain.Location;
import eu.socialsensor.framework.common.domain.MediaItem;
import eu.socialsensor.framework.common.domain.StreamUser;
import eu.socialsensor.framework.common.domain.StreamUser.Category;
import eu.socialsensor.framework.common.domain.dysco.Entity;
import eu.socialsensor.framework.common.domain.dysco.Entity.Type;
import eu.socialsensor.framework.common.factories.ItemFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.solr.client.solrj.beans.Field;

/**
 *
 * @author etzoannos - e.tzoannos@atc.gr
 */
public class SolrItem {

    public SolrItem() {
    }

    public SolrItem(Item item) {

        id = item.getId();
        streamId = item.getStreamId();
        title = item.getTitle();
        description = item.getDescription();
        tags = item.getTags();
        source = item.getUrl();
        StreamUser streamUser = item.getStreamUser();
        if (streamUser != null) {
            author = streamUser.getUsername();
            //author = streamUser.getId();
        }

        links = new ArrayList<String>();
        if (item.getLinks() != null) {
            for (URL link : item.getLinks()) {
                links.add(link.toString());
            }
        }

        mediaLinks = new ArrayList<String>();
        if (item.getMediaItems() != null) {
            for (MediaItem mediaItem : item.getMediaItems()) {
                mediaLinks.add(mediaItem.getUrl() + "%%" + mediaItem.getThumbnail());
            }
        }

        //this is long
        publicationTime = item.getPublicationTime();

        //List<String> peopleTemp = extractPeople(item.getTitle());
        //peopleTemp.add("@" + item.getAuthorScreenName());
        //people = peopleTemp;
        comments = item.getComments();
        latitude = item.getLatitude();
        longitude = item.getLongitude();
        location = item.getLocationName();
        sentiment = item.getSentiment();
        language = item.getLang();

        positiveVotes = item.getPositiveVotes();
        negativeVotes = item.getNegativeVotes();

        List<Entity> entities = item.getEntities();

        if (entities != null) {

            for (Entity entity : entities) {
                if (entity.getType() == Type.LOCATION) {
                    locationEntities.add(entity.getName());
                } else if (entity.getType() == Type.PERSON) {
                    personEntities.add(entity.getName());
                } else if (entity.getType() == Type.ORGANIZATION) {
                    organizationEntities.add(entity.getName());
                }
            }

        }

        //this is a map
        mediaIds = new ArrayList<String>();
        if (item.getMediaIds() != null) {
            mediaIds.addAll(item.getMediaIds());
        }

        //the following derive from alethiometer
//        Score fullScore = item.getFullScore();
//        if (fullScore != null) {
//            alethiometerScore = fullScore.getScore();
//            alethiometerUserScore = fullScore.getTotalContributorScore();
//        } else {
//            alethiometerScore = -1;
//            alethiometerUserScore = -1;
//        }
        alethiometerScore = item.getAlethiometerScore();
        alethiometerUserScore = item.getAlethiometerUserScore();
        alethiometerUserStatus = item.getAlethiometerUserStatus();
        userRole = item.getUserRole();
        original = item.isOriginal();

        Category cat = item.getCategory();
        if (cat != null) {
            category = cat.name();
        }

        StreamUser user = item.getStreamUser();
        if (user != null) {
            authorFullName = user.getName();
            authorScreenName = user.getUsername();
            avatarImage = user.getImageUrl();
            avatarImageSmall = user.getProfileImage();

//            if (user.getCategory() != null) {
//                category = user.getCategory().name();
//            }
            Long followers = user.getFollowers();
            if (followers != null) {
                followersCount = followers.intValue();
            }
            Long friends = user.getFriends();
            if (friends != null) {
                friendsCount = friends.intValue();
            }

        }

        lists = new ArrayList<String>();
        if (item.getList() != null) {
            lists.addAll(Arrays.asList(item.getList()));
        }

        validityScore = item.getValidityScore();

        //convert votes to JSONString and put it to SolrItem
        String itemVotes = new Gson().toJson(item.getVotes());
        validityVotes = itemVotes;

        retweetsCount = item.getShares().intValue();

        originalTitle = item.getOriginalTitle();

        popularityComments = item.getNumOfComments();
        popularityShares = item.getShares();
        popularityLikes = item.getLikes();
        if (item.getStreamId().equals("Twitter")) {
            popularity = item.getShares();
        } else if (item.getStreamId().equals("Facebook")) {
            popularity = item.getLikes() * 2L;
        }
    }

    public Item toItem() throws MalformedURLException {

        Item item = new Item();

        item.setNumOfComments(popularityComments);
        item.setLikes(popularityLikes);
        item.setShares(popularityShares);

        item.setValidityScore(validityScore);
        item.setVotes(ItemFactory.createVoteList(validityVotes));
        item.setPositiveVotes(positiveVotes);
        item.setNegativeVotes(negativeVotes);

        item.setOriginalTitle(originalTitle);

        item.setId(id);
        item.setStreamId(streamId);
        item.setTitle(title);
        item.setDescription(description);
        item.setTags(tags);
        item.setOriginal(original);
        item.setUrl(source);

        if (links != null) {
            URL[] _links = new URL[links.size()];
            for (int i = 0; i < links.size(); i++) {
                _links[i] = new URL(links.get(i));
            }
            item.setLinks(_links);
        }

        item.setPublicationTime(publicationTime);

        item.setComments(comments);

        if (latitude != null && longitude != null) {
            item.setLocation(new Location(latitude, longitude, location));
        } else {
            item.setLocation(new Location(location));
        }

        if (mediaIds != null) {
            item.setMediaIds(mediaIds);
        }

        item.setAlethiometerScore(alethiometerScore);
        item.setAlethiometerUserScore(alethiometerUserScore);
        item.setUserRole(userRole);
        item.setAuthorFullName(authorFullName);
        item.setFollowersCount(followersCount);
        item.setFriendsCount(friendsCount);
        item.setAvatarImage(avatarImage);
        item.setAvatarImageSmall(avatarImageSmall);
        item.setAuthorScreenName(authorScreenName);
        item.setLang(language);

        if (category != null) {
            if (category.equals("politician")) {
                item.setCategory(Category.politician);
            } else if (category.equals("footballer")) {
                item.setCategory(Category.footballer);
            } else if (category.equals("official")) {
                item.setCategory(Category.official);
            } else if (category.equals("journalist")) {
                item.setCategory(Category.journalist);
            }
        }

        item.setAlethiometerUserStatus(alethiometerUserStatus);
        item.setShares(new Long(retweetsCount));

        return item;
    }
    @Field(value = "id")
    private String id;
    @Field(value = "streamId")
    private String streamId;
    @Field(value = "source")
    private String source;
    @Field(value = "title")
    private String title;
    @Field(value = "description")
    private String description;
    @Field(value = "tags")
    private String[] tags;
    @Field(value = "categories")
    private String[] categories;
    @Field(value = "author")
    private String author;
    //@Field(value = "people")
    //private List<String> people;
    @Field(value = "links")
    private List<String> links;
    @Field(value = "mediaLinks")
    private List<String> mediaLinks;
    @Field(value = "publicationTime")
    private long publicationTime;
    @Field(value = "comments")
    private String[] comments;
    @Field(value = "latitude")
    private Double latitude;
    @Field(value = "longitude")
    private Double longitude;
    @Field(value = "location")
    private String location;
    @Field(value = "mediaIds")
    private List<String> mediaIds;
    // new fields added:27.3.2013
    @Field(value = "sentiment")
    private String sentiment;
    // the following fields are added for the UI purposes (after retrieval from Solr)
    // no need to be populated at crawling time
    @Field(value = "alethiometerScore")
    private int alethiometerScore = -1;
    @Field(value = "alethiometerUserScore")
    private int alethiometerUserScore = -1;
    @Field(value = "authorFullName")
    private String authorFullName;
    @Field(value = "userRole")
    private String userRole;
    @Field(value = "followersCount")
    private int followersCount = 0;
    @Field(value = "friendsCount")
    private int friendsCount = 0;
    @Field(value = "avatarImage")
    private String avatarImage;
    @Field(value = "avatarImageSmall")
    private String avatarImageSmall;
    @Field(value = "authorScreenName")
    private String authorScreenName;
    @Field(value = "language")
    private String language;
    @Field(value = "category")
    private String category;
    @Field(value = "original")
    private boolean original;
    @Field(value = "alethiometerUserStatus")
    private String alethiometerUserStatus;
    @Field(value = "validityScore")
    private int validityScore;
    @Field(value = "validityVotes")
    private String validityVotes;
    @Field(value = "positiveVotes")
    private int positiveVotes;
    @Field(value = "negativeVotes")
    private int negativeVotes;
    @Field(value = "retweetsCount")
    private int retweetsCount = 0;
    @Field(value = "lists")
    private List<String> lists;
    @Field(value = "originalTitle")
    private String originalTitle;
    //popularity fields
    @Field(value = "popularityLikes")
    private Long popularityLikes = 0L;
    @Field(value = "popularityShares")
    private Long popularityShares = 0L;
    @Field(value = "popularityComments")
    private Long popularityComments = 0L;
    @Field(value = "popularity")
    private Long popularity = 0L;
    @Field(value = "locationEntities")
    private List<String> locationEntities;
    @Field(value = "personEntities")
    private List<String> personEntities;
    @Field(value = "organizationEntities")
    private List<String> organizationEntities;

    public List<String> getLocationEntities() {
        return locationEntities;
    }

    public void setLocationEntities(List<String> locationEntities) {
        this.locationEntities = locationEntities;
    }

    public List<String> getPersonEntities() {
        return personEntities;
    }

    public void setPersonEntities(List<String> personEntities) {
        this.personEntities = personEntities;
    }

    public List<String> getOrganizationEntities() {
        return organizationEntities;
    }

    public void setOrganizationEntities(List<String> organizationEntities) {
        this.organizationEntities = organizationEntities;
    }

    public Long getPopularityLikes() {
        return popularityLikes;
    }

    public void setPopularityLikes(Long popularityLikes) {
        this.popularityLikes = popularityLikes;
    }

    public Long getPopularityShares() {
        return popularityShares;
    }

    public void setPopularityShares(Long popularityShares) {
        this.popularityShares = popularityShares;
    }

    public Long getPopularityComments() {
        return popularityComments;
    }

    public void setPopularityComments(Long popularityComments) {
        this.popularityComments = popularityComments;
    }

    public Long getPopularity() {
        return popularity;
    }

    public void setPopularity(Long popularity) {
        this.popularity = popularity;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public int getPositiveVotes() {
        return positiveVotes;
    }

    public void setPositiveVotes(int positiveVotes) {
        this.positiveVotes = positiveVotes;
    }

    public int getNegativeVotes() {
        return negativeVotes;
    }

    public void setNegativeVotes(int negativeVotes) {
        this.negativeVotes = negativeVotes;
    }

    public String getAlethiometerUserStatus() {
        return alethiometerUserStatus;
    }

    public void setAlethiometerUserStatus(String alethiometerUserStatus) {
        this.alethiometerUserStatus = alethiometerUserStatus;
    }

    public boolean isOriginal() {
        return original;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAuthorScreenName() {
        return authorScreenName;
    }

    public void setAuthorScreenName(String authorScreenName) {
        this.authorScreenName = authorScreenName;
    }

    public int getAlethiometerScore() {
        return alethiometerScore;
    }

    public void setAlethiometerScore(int alethiometerScore) {
        this.alethiometerScore = alethiometerScore;
    }

    public int getAlethiometerUserScore() {
        return alethiometerUserScore;
    }

    public void setAlethiometerUserScore(int alethiometerUserScore) {
        this.alethiometerUserScore = alethiometerUserScore;
    }

    public String getAuthorFullName() {
        return authorFullName;
    }

    public void setAuthorFullName(String authorFullName) {
        this.authorFullName = authorFullName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public String getAvatarImage() {
        return avatarImage;
    }

    public void setAvatarImage(String avatarImage) {
        this.avatarImage = avatarImage;
    }

    public String getAvatarImageSmall() {
        return avatarImageSmall;
    }

    public void setAvatarImageSmall(String avatarImageSmall) {
        this.avatarImageSmall = avatarImageSmall;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //public List<String> getPeople() {
    //    return people;
    //}
    //public void setPeople(List<String> people) {
    //    this.people = people;
    //}
    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public List<String> getMediaLinks() {
        return mediaLinks;
    }

    public void setMediaLinks(List<String> mediaLinks) {
        this.mediaLinks = mediaLinks;
    }

    public Long getPublicationTime() {
        return publicationTime;
    }

    public void setPublicationTime(Long publicationTime) {
        this.publicationTime = publicationTime;
    }

    public String[] getComments() {
        return comments;
    }

    public void setComments(String[] comments) {
        this.comments = comments;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getMediaIds() {
        return mediaIds;
    }

    public void setMediaIds(List<String> mediaIds) {
        this.mediaIds = mediaIds;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public int getValidityScore() {
        return validityScore;
    }

    public void setValidityScore(int validityScore) {
        this.validityScore = validityScore;
    }

    public String getValidityVotes() {
        return validityVotes;
    }

    public void setValidityVotes(String validityVotes) {
        this.validityVotes = validityVotes;
    }

    public List<String> getLists() {
        return lists;
    }

    public void setLists(List<String> lists) {
        this.lists = lists;
    }

    private List<String> extractPeople(String input) {

        // String to be scanned to find the pattern.
        String pattern = "(?:\\s|\\A)[@]+([A-Za-z0-9-_]+)";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(input);

        List<String> out = new ArrayList<String>();
        while (m.find()) {
            out.add(m.group());
        }
        return out;
    }

    public static void main(String args[]) {

        // String to be scanned to find the pattern.
        String line = "@user user 1, asdfasf ,  @safas ,saf asdf@ sfdasf@asdfas  asfasf asfas asfsd";
        String pattern = "(?:\\s|\\A)[@]+([A-Za-z0-9-_]+)";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(line);

        while (m.find()) {

            System.out.println("Found value: " + m.group());

        }
    }
}
