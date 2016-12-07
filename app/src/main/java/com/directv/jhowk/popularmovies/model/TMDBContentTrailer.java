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
    private String mStringName;
    private String mStringVideoSize;
    private String mStringType;
    private String mStringSource;

    public TMDBContentTrailer(JSONObject jsonObject) {
        mJSONObject = jsonObject;
        try {
            mStringName = mJSONObject.getString(NAME);
            mStringVideoSize = mJSONObject.getString(SIZE);
            mStringSource = mJSONObject.getString(SOURCE);
            mStringType = mJSONObject.getString(TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return String.format("TMDBContentTrailer[Name:%s,Source:%s]",mStringName,mStringSource);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties
    ///////////////////////////////////////////////////////////////////////////

    public String getStringName() {
        return mStringName;
    }

    public void setStringName(String stringName) {
        mStringName = stringName;
    }

    public String getStringVideoSize() {
        return mStringVideoSize;
    }

    public void setStringVideoSize(String stringVideoSize) {
        mStringVideoSize = stringVideoSize;
    }

    public String getStringType() {
        return mStringType;
    }

    public void setStringType(String stringType) {
        mStringType = stringType;
    }

    public String getStringSource() {
        return mStringSource;
    }

    public void setStringSource(String stringSource) {
        mStringSource = stringSource;
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
        dest.writeString(this.mStringName);
        dest.writeString(this.mStringVideoSize);
        dest.writeString(this.mStringType);
        dest.writeString(this.mStringSource);
    }

    protected TMDBContentTrailer(Parcel in) {
        try {
            this.mJSONObject = new JSONObject(in.readString());
        } catch (JSONException e) {
            e.printStackTrace();
            this.mJSONObject = null;
        }
        this.mStringName = in.readString();
        this.mStringVideoSize = in.readString();
        this.mStringType = in.readString();
        this.mStringSource = in.readString();
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
