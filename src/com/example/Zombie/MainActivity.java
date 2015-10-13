package com.example.Zombie;

import com.google.devtools.simple.runtime.components.Component;
import com.google.devtools.simple.runtime.components.HandlesEventDispatching;
import com.google.devtools.simple.runtime.components.android.*;
import com.google.devtools.simple.runtime.events.EventDispatcher;

public class MainActivity extends Form implements HandlesEventDispatching {
    private Label touchZombieLabel;
    private HorizontalArrangement horizontalArrangement;
    private Button zombieButton;
    private Sound zombieMoanSound;
    private Sound zombieAttackSound;
    private AccelerometerSensor accelerometerSensor;
    private LocationSensor locationSensor;
    private Notifier notifier;
    private ActivityStarter activityStarter;
    private TextToSpeech textToSpeech;

    private int zombieImageHeight = 712;
    private int zombieImageWidth = 855;

    private String latitude, longitude;
    private String gpsProviderName;

    void $define() {
        this.ScreenOrientation("portrait");

        // Set the screen name title
        this.Title("Hello Zombie");

        // Set the screens background color and background image
        this.BackgroundColor(COLOR_NONE);
        this.BackgroundImage("SplatterBackground.png");
        this.Scrollable(false);

        textToSpeech = new TextToSpeech(this);

        // Create the label and put it on the screen in the center
        touchZombieLabel = new Label(this);
        touchZombieLabel.Text("Touch the Zombie");
        touchZombieLabel.FontSize(20.f);
        touchZombieLabel.Width(LENGTH_FILL_PARENT);
        touchZombieLabel.TextAlignment(Component.ALIGNMENT_CENTER);
        touchZombieLabel.FontBold(true);

        horizontalArrangement = new HorizontalArrangement(this);
        horizontalArrangement.Width(LENGTH_FILL_PARENT);
        horizontalArrangement.Height(LENGTH_FILL_PARENT);

        zombieButton = new Button(horizontalArrangement);
        zombieButton.Image("Zombie.png");
        zombieButton.Width(zombieImageWidth);
        zombieButton.Height(zombieImageHeight);
        zombieButton.TextAlignment(Component.ALIGNMENT_CENTER);

        // Initialize Zombie attack sound
        zombieAttackSound = new Sound(this);
        zombieAttackSound.Source("ZombieAttack.wav");

        // Make zombie moan when it is clicked
        zombieMoanSound = new Sound(this);
        zombieMoanSound.Source("ZombieMoan.wav");

        EventDispatcher.registerEventForDelegation(this, "ButtonClick", "Click");

        locationSensor = new LocationSensor(this);
        locationSensor.RefreshProvider();

        notifier = new Notifier(this);

        activityStarter = new ActivityStarter(this);

        accelerometerSensor = new AccelerometerSensor(this);

        EventDispatcher.registerEventForDelegation(this, "ShakingPhone", "Shaking");
    }

    @Override
    public boolean dispatchEvent(Component component, String id, String eventName, Object[] args) {
        if (component.equals(zombieButton) && eventName.equals("Click")) {
            zombieMoanSound.Vibrate(500);
            zombieMoanSound.Play();

            gpsProviderName = locationSensor.ProviderName();

            if (!gpsProviderName.equals("gps")) {
                notifier.ShowAlert("Enable GPS in Settings");


                // Open the settings where the user enables GPS
                activityStarter.Action("android.settings.LOCATION_SOURCE_SETTINGS");
                activityStarter.StartActivity();

            }
            latitude = Double.toString(locationSensor.Latitude());
            longitude = Double.toString(locationSensor.Longitude());


            // Convert text provided to speech
            textToSpeech.Speak("Zombies latitude is " + latitude + " Zombies longitude is " + longitude);

            return true;
        } else if( component.equals(accelerometerSensor) && eventName.equals("Shaking")){
            zombieAttackSound.Play();
            return true;
        }
        return false;
    }

}
