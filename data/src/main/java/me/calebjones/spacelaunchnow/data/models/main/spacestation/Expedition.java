package me.calebjones.spacelaunchnow.data.models.main.spacestation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.AstronautFlight;

public class Expedition extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("start")
    @Expose
    public Date start;
    @SerializedName("end")
    @Expose
    public Date end;
    @SerializedName("spacestation")
    @Expose
    public Spacestation spacestation;
    @SerializedName("crew")
    @Expose
    public RealmList<AstronautFlight> crew = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Spacestation getSpacestation() {
        return spacestation;
    }

    public void setSpacestation(Spacestation spacestation) {
        this.spacestation = spacestation;
    }

    public RealmList<AstronautFlight> getCrew() {
        return crew;
    }

    public void setCrew(RealmList<AstronautFlight> crew) {
        this.crew = crew;
    }
}
