package yasuaki.kyoto.com.simpledialog.di.component;

import dagger.Component;
import yasuaki.kyoto.com.simpledialog.di.PerActivity;
import yasuaki.kyoto.com.simpledialog.di.module.ActivityModule;
import yasuaki.kyoto.com.simpledialog.ui.MainActivity;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity activity);
}