package com.directv.jhowk.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Object representing a particular content trailer.  For now, this will only focus on
 * Youtube trailers, and disregard all others.
 *
 * Created by Jason Howk.
 */
public class TMDBContentTrailer implements Parcelable {
    private static final String LOG_TAG = TMDBContentTrailer.class.getSimpleName();

    private static final String NAME = "name";
    private static final String SIZE = "size";
    private static final String SOURCE = "source";
    private static final String TYPE = "type";

    private JSONObject mJSONObject;
    private String mName;
    private String mSize;
    private String mType;
    private String mSource;

    public TMDBContentTrailer(JSONObject jsonObject) {
        mJSONObject = jsonObject;
        try {
            mName = mJSONObject.getString(NAME);
            mSize = mJSONObject.getString(SIZE);
            mSource = mJSONObject.getString(SOURCE);
            mType = mJSONObject.getString(TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return String.format("TMDBContentTrailer[Name:%s,Source:%s]",mName,mSource);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties
    ///////////////////////////////////////////////////////////////////////////

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSize() {
        return mSize;
    }

    public void setSize(String size) {
        mSize = size;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getSource() {
        return mSource;
    }

    public void setSource(String source) {
        mSource = source;
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
        dest.writeString(this.mName);
        dest.writeString(this.mSize);
        dest.writeString(this.mType);
        dest.writeString(this.mSource);
    }

    protected TMDBContentTrailer(Parcel in) {
        try {
            this.mJSONObject = new JSONObject(in.readString());
        } catch (JSONException e) {
            e.printStackTrace();
            this.mJSONObject = null;
        }
        this.mName = in.readString();
        this.mSize = in.readString();
        this.mType = in.readString();
        this.mSource = in.readString();
    }

    public static final Parcelable.Creator<TMDBContentTrailer> CREATOR = new Parcelable.Creator<TMDBContentTrailer>() {
        @Override
        public TMDBContentTrailer createFromParcel(Parcel source) {
            return new TMDBContentTrailer(source);
        }

        @Override
        public TMDBContentTrailer[] newArray(int size) {
            return new TMDBContentTrailer[size];
        }
    };
}
