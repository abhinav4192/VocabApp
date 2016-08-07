package fightingpit.VocabBuilder.Engine;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.Locale;

import fightingpit.VocabBuilder.R;

/**
 * Created by abhinavgarg on 03/07/16.
 */
public class TextToSpeechManager implements TextToSpeech.OnInitListener {

    private TextToSpeech mTextToSpeech;
    private Context mContext = ContextManager.getCurrentActivityContext();
    private Boolean mIsShutDownCalled = false;
    // mToInstallTTS -  makes sure that user is not prompted for TTSM install unless they
    // requested for word pronunciation.
    private Boolean mToInstallTTS = false;
    // mIsReady - true is TTS engine is ready to convert text to speech.
    private Boolean mIsReady = false;

    /**
     * Initialize Text to Speech. Finds out if TTS engine present on device or not.
     */
    public void init() {
        mIsShutDownCalled = false;
        Intent aCheckTTSIntent = new Intent();
        aCheckTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        ((Activity) mContext).startActivityForResult(aCheckTTSIntent, mContext.getResources()
                .getInteger(R.integer.CHECK_TTS_ENGINE));
    }

    /**
     * To be called by the onActivityResult() of the activity which instantiated
     * TextToSpeechManager. If TTS engine is found, create new TTS instance.
     *
     * If TTS engine is not found, prompt user to install TTS only if he had requested for
     * pronunciation.
     */
    public void onActivityResult(int resultCode, Intent data) {
        if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
            // success, create the TTS instance. OnInit will be called with result status.
            mTextToSpeech = new TextToSpeech(mContext, this);
        } else {
            // missing data, install it
            if(mToInstallTTS){
                installTTS();
            }
        }
    }

    /**
     * Called after a creating a new TextToSpeech object. If onInit is called, it means  TTS
     * engine is present. Checks for language present. If English UK or English US is present,
     * set status of TTS engine as ready in mIsReady boolean.
     *
     * If languages not present, prompt user to install TTS languages only if he had requested for
     * pronunciation.
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            if (mTextToSpeech.isLanguageAvailable(Locale.UK) == TextToSpeech
                    .LANG_COUNTRY_AVAILABLE ||
                    mTextToSpeech.isLanguageAvailable(Locale.UK) == TextToSpeech
                            .LANG_COUNTRY_VAR_AVAILABLE) {
                mTextToSpeech.setLanguage(Locale.UK);
                mIsReady = true;
            }else if (mTextToSpeech.isLanguageAvailable(Locale.US) == TextToSpeech
                    .LANG_COUNTRY_AVAILABLE ||
                    mTextToSpeech.isLanguageAvailable(Locale.US) == TextToSpeech
                            .LANG_COUNTRY_VAR_AVAILABLE) {
                mTextToSpeech.setLanguage(Locale.US);
                mIsReady = true;
            } else {
                if(mToInstallTTS){
                    installTTS();
                }
            }
        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(mContext, "Sorry! Speech failed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Covert Text to speech. Will convert only if shutdown is not called earlier.
     * Checks if TTS engine is ready. If not, set that TTS install is required as user has
     * requested for pronunciation and then init again.
     *
     * @param word to be spoken.
     */
    public void speak(String word) {
        if (!mIsShutDownCalled) {
            if (!mIsReady) {
                mToInstallTTS = true;
                init();
            } else if (mTextToSpeech != null && mIsReady) {
                mTextToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    /**
     * Shutdown TTS. Do not forget to call.
     */
    public void shutdown() {
        mIsShutDownCalled = true;
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
    }

    /**
     * AlertDialog to prompt user to install missing TTS Engine/Languages.
     */
    private void installTTS() {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(mContext);
        aBuilder.setMessage("Voice data not found. Please install voice data for English (United " +
                "States) or English (United Kingdom). Connect to Wi-Fi to install.");
        aBuilder.setCancelable(true);
        aBuilder.setNeutralButton("Install",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent installIntent = new Intent();
                        installIntent.setAction(
                                TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                        mContext.startActivity(installIntent);
                    }
                });

        AlertDialog aAlertDialog = aBuilder.create();
        aAlertDialog.show();
    }
}
