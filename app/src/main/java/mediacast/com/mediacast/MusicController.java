package mediacast.com.mediacast;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.MediaController;

/**
 * Created by David on 02/03/2016.
 */
public class MusicController extends MediaController {

    public MusicController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MusicController(Context context) {
        super(context);
    }

    public void hide() {}
}
