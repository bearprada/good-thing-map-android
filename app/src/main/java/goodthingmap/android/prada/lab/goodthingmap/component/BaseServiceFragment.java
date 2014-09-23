package goodthingmap.android.prada.lab.goodthingmap.component;

import android.prada.lab.goodthingmap.network.GoodThingService;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import goodthingmap.android.prada.lab.goodthingmap.R;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by prada on 2014/7/4.
 */
public abstract class BaseServiceFragment extends Fragment {
    protected ParseUser mUser;
    protected final GoodThingService mService;

    public BaseServiceFragment() {
        mUser = ParseUser.getCurrentUser();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://vRgutJGNaqr9sgjrCp5z6ltoyOr47QOqkiwQD9q2:javascript-key=" +
                        "9urAGbTvVsTSvTkqjAWeL0xxiUfnEbIz7W3MfbEm@api.parse.com/1/functions")
                .setRequestInterceptor(mRequestInterceptor)
                .build();
        mService = restAdapter.create(GoodThingService.class);
    }

    public boolean isFacebookLogin() {
        Session session = ParseFacebookUtils.getSession();
        return session != null && session.isOpened();
    }

    public void facebookLogin() {
        if (isFacebookLogin()) {
            linkToFacebook();
        } else {
            ParseFacebookUtils.logIn(Arrays.asList(ParseFacebookUtils.Permissions.User.ABOUT_ME,
                            ParseFacebookUtils.Permissions.User.EMAIL,
                            ParseFacebookUtils.Permissions.User.BIRTHDAY
                    ),
                    getActivity(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException err) {
                            if(err == null) {
                                linkToFacebook();
                            } else {
                                err.printStackTrace();
                            }
                        }
                    }
            );
        }
    }

    public void linkToFacebook() {
        updateUserProfile();
        if (!ParseFacebookUtils.isLinked(mUser)) {
            ParseFacebookUtils.link(mUser, getActivity(), new SaveCallback() {
                @Override
                public void done(ParseException ex) {
                    if (ParseFacebookUtils.isLinked(mUser)) {
                        updateUserProfile();
                    }
                }
            });
        } else {
            updateUserProfile();
        }
    }

    public void updateUserProfile() {
        if (isFacebookLogin()) {
            Request request = Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        mUser.setUsername(user.getUsername());
                        mUser.put("facebookId", user.getId());
                        mUser.put("birthday", user.getBirthday());
                        mUser.put("name", user.getName());

                        if(user.getProperty("gender") != null) {
                            mUser.put("gender", (String)user.getProperty("gender"));
                        }

                        if(user.getProperty("email") != null) {
                            mUser.setEmail((String)user.getProperty("email"));
                        }

                        if(user.getProperty("age_range") != null) {
                            mUser.put("ageRange", (String)user.getProperty("age_range"));
                        }

                        if(user.getProperty("hometown") != null) {
                            mUser.put("hometown", (String)user.getProperty("hometown"));
                        }

                        if(user.getProperty("location") != null) {
                            mUser.put("location", (String)user.getProperty("location"));
                        }

                        if(user.getProperty("education") != null) {
                            mUser.put("education", (String)user.getProperty("education"));
                        }

                        if(user.getProperty("relationship_status") != null) {
                            mUser.put("relationshipStatus", (String)user.getProperty("relationship_status"));
                        }

                       mUser.saveEventually();
                    }
                }
            });
            request.executeAsync();
        }
    }

    protected RequestInterceptor mRequestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("X-Parse-Application-Id", "vRgutJGNaqr9sgjrCp5z6ltoyOr47QOqkiwQD9q2");
            request.addHeader("X-Parse-REST-API-Key", "QZOzLqgfsZFpwXFx86O4emGNWcVED6YlEG88aPzG");
        }
    };
}
