package com.example.brona.picasaconnect;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static com.example.brona.picasaconnect.MainActivity.context;

public class AuthenticationManager {

    private static final String TAG = AuthenticationManager.class.getSimpleName();
    private static final String PICASA_WEB_SERVICE = "lh2"; // Picasa Web Albums service name
    private static final String GOOGLE_ACCOUNT_TYPE = "com.google";
    private static final int PICK_ACCOUNT_REQUEST = 1;
    private static final int AUTHENTICATE_REQUEST = 2;

    private Activity mActivityContext;
    private AccountManager mAccountManager;
    private Account mSelectedAccount;
    private DataManager dataManager;
    private TokenListener mTokenListener= new TokenListener() {
        @Override
        public void onTokenAcquired(String token, String accountName) {
            Log.d(TAG, String.format("onTokenAcquired token: %s, accountName: %s", token, accountName));
            HTTPReq httpReq = new HTTPReq();
            httpReq.httpReq();
            dataManager = new DataManager(new PicasaService(context));
            dataManager.setAccountName(context, accountName);
            dataManager.setToken(token);
            dataManager.getAlbums();
            Observable<List<Photo>> albums = dataManager.getPhotos("6475969304088902209").map(new Function<Response<PhotoResponse>, List<Photo>>() {
                @Override
                public List<Photo> apply(Response<PhotoResponse> response) throws Exception {
                    int code = response.raw().networkResponse().code();
                    if (code == 304) {
                        return Collections.<Photo>emptyList();
                    } else {
                        return response.body().getPhotoFeed().getPhotoList();
                    }
                }
            })
                    .filter(new Predicate<List<Photo>>() {
                        @Override
                        public boolean test(List<Photo> photos) throws Exception {
                            return !photos.isEmpty();
                        }
                    })
                    .subscribeOn(Schedulers.io());

          //  List<Photo> photos = albums.blockingFirst();
            //Log.d("album photos","" + photos.size());
            DisposableObserver<List<Photo>> subscriber = new DisposableObserver<List<Photo>>() {
                @Override
                public void onNext(List<Photo> photos) {
                    Log.d("album photos", photos.toString());
                    for(int i =0; i <photos.size(); i++){
                        Photo pic = photos.get(i);
                        Log.d("album photo", pic.toString());
                    }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {
                }
            };

            albums.observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(subscriber);


        }


        @Override
        public void onTokenError(String message) {

        }
    };
    private String mToken;

    public interface TokenListener {
        void onTokenAcquired(String token, String accountName);
        void onTokenError(String message);
    }

    public AuthenticationManager(Activity activity) {
        mActivityContext = activity;
        mAccountManager = (AccountManager) mActivityContext.getSystemService(MainActivity.ACCOUNT_SERVICE);
    }

    public void setTokenListener(TokenListener listener) {
        mTokenListener = listener;
    }

    public void dispose() {
        Log.d(TAG, "dispose");
        setTokenListener(null);
        mActivityContext = null;
    }

    // show Choose Account picker
    public static void showAccountPicker(Activity activity) {
        Intent intent = AccountManager.newChooseAccountIntent(null, null,
                new String[]{GOOGLE_ACCOUNT_TYPE}, null, null, null, null);
        activity.startActivityForResult(intent, PICK_ACCOUNT_REQUEST);
    }


    // this can get called in two cases:
    // 1) when Choose Account completes it calls the client activity that called showAccountPicker
    // 2) when requestToken fails and returns Intent to launch authentication dialog
    public void onAccountPickerResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            Log.e(TAG, "onAccountPickerResult - result != OK");
            return;
        }
        switch(requestCode) {
            // user picked account, now use that to request token
            case PICK_ACCOUNT_REQUEST:
                String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                mAccountManager.invalidateAuthToken(GOOGLE_ACCOUNT_TYPE, mToken);
                requestToken(findAccount(mAccountManager, accountName));
                break;
            // user needed to enter password.
            case AUTHENTICATE_REQUEST:
                requestToken(mSelectedAccount);
                break;
        }
    }

    // this version for use in okhttp interceptor error handling
    private static Account findAccount(AccountManager am, String accountName) {
        Account[] accounts;
        try {
            accounts = am.getAccounts();
            for (Account account:accounts) {
                if (account.name.equals(accountName)) {
                    return account;
                }
            }
            Log.e(TAG, "findAccount - failed for accountName: " + accountName);
            return null;

        } catch (SecurityException e) {
            Log.e(TAG, "findAccount - permissions check GET_ACCOUNTS failed");
            return null;
        }
    }

    private static Account findAccount(Context context, String accountName) {
        return findAccount(AccountManager.get(context), accountName);
    }

    public static String blockingRequestToken(AccountManager accountManager, String accountName, String oldToken) {
        Log.d(TAG, "blockingRequestToken");
        Account account = findAccount(accountManager, accountName);
        String token = "";
        if (account != null) {
            accountManager.invalidateAuthToken(GOOGLE_ACCOUNT_TYPE, oldToken);
            try {
                token = accountManager.blockingGetAuthToken(account, PICASA_WEB_SERVICE, true);
                Log.d(TAG, "backgroundRequestToken new token: " + token);
            } catch (Exception e) {
                Log.e(TAG, "backgroundRequestToken -  " + e.getMessage());
            }
        }
        return token;
    }

    // Get auth token for Picasa Web service
    private void requestToken(Account account) {
        if (account != null) {
            Log.d(TAG, "requestToken for account: " + account.name);
            mSelectedAccount = account;
            mAccountManager.getAuthToken(
                    mSelectedAccount,
                    PICASA_WEB_SERVICE,                       // Authentication token type or scope
                    null,                                     // Authenticator-specific options
                    mActivityContext,                         // activity - used to prompt user for password if necessary
                    new OnTokenAcquired(),                    // Callback called when request completes
                    null);                                    // Handler identifying the callback thread, null for the main thread
        } else {
            // todo string
            mTokenListener.onTokenError("Null account in requestToken");
        }
    }

    public void requestToken(Context context, String accountName) {
        Account account = findAccount(context, accountName);
        requestToken(account);
    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            try {
                Log.d(TAG, "OnTokenAcquired");
                Bundle bundle = result.getResult();
                // if we didn't get the token, need password from user. start intent to request password
                if (bundle.containsKey(AccountManager.KEY_INTENT)) {
                    Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
                    if (intent != null) {
                        int flags = intent.getFlags();
                        flags &= ~Intent.FLAG_ACTIVITY_NEW_TASK;
                        intent.setFlags(flags);
                        mActivityContext.startActivityForResult(intent, AUTHENTICATE_REQUEST);
                    }
                } else if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                    String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
                    mToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                    Log.i("authtag", accountName);
                    Log.i("authtag", mToken.toString());
                    mTokenListener.onTokenAcquired(mToken, accountName);
                }
            }

            // cached token has expired - try to get a new one
            catch (AuthenticatorException e) {
                Log.e(TAG, "OnTokenAcquired - AuthenticatorException: " + e.getMessage());
                mAccountManager.invalidateAuthToken(GOOGLE_ACCOUNT_TYPE, mToken);
                requestToken(mSelectedAccount);
            }
            catch (OperationCanceledException e) {
                Log.d(TAG, "OnTokenAcquired - User cancelled");
            }
            catch (IOException e) {
                String msg = e.getMessage();
                Log.e(TAG, msg);
                mTokenListener.onTokenError(msg);
            }
        }
    }
}
