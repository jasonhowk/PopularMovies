package com.directv.jhowk.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Object representing a review of a content item.
 *
 * Created by Jason Howk.
 */
public class TMDBContentReview implements Parcelable {
    private static final String LOG_TAG = TMDBContentReview.class.getSimpleName();

    private static final String ID = "id";
    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";
    private static final String URL= "url";

    private JSONObject mJSONObject;
    private String mId;
    private String mAuthor;
    private String mContent;
    private URL mURL;

    public TMDBContentReview(String id, String author, String content, URL URL) {
        mId = id;
        mAuthor = author;
        mContent = content;
        mURL = URL;
    }

    public TMDBContentReview(JSONObject jsonObject) {
        mJSONObject = jsonObject;
        try {
            mId = mJSONObject.getString(ID);
            mAuthor = mJSONObject.getString(AUTHOR);
            mContent = mJSONObject.getString(CONTENT);
            mURL = new URL(mJSONObject.getString(URL));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return String.format("TMDBContentReview[Id:%s,URL:%s]",mId,mURL.toString());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties
    ///////////////////////////////////////////////////////////////////////////
    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public java.net.URL getURL() {
        return mURL;
    }

    public void setURL(java.net.URL URL) {
        mURL = URL;
    }


    ///////////////////////////////////////////////////////////////////////////
    // Parcelable
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mJSONObject.toString());
        dest.writeString(this.mId);
        dest.writeString(this.mAuthor);
        dest.writeString(this.mContent);
        dest.writeSerializable(this.mURL);
    }

    protected TMDBContentReview(Parcel in) {
        try {
            this.mJSONObject = new JSONObject(in.readString());
        } catch (JSONException e) {
            e.printStackTrace();
            this.mJSONObject = null;
        }
        this.mId = in.readString();
        this.mAuthor = in.readString();
        this.mContent = in.readString();
        this.mURL = (java.net.URL) in.readSerializable();
    }

    public static final Parcelable.Creator<TMDBContentReview> CREATOR = new Parcelable.Creator<TMDBContentReview>() {
        @Override
        public TMDBContentReview createFromParcel(Parcel source) {
            return new TMDBContentReview(source);
        }

        @Override
        public TMDBContentReview[] newArray(int size) {
            return new TMDBContentReview[size];
        }
    };
}
