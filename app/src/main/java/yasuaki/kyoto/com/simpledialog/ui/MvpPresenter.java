package yasuaki.kyoto.com.simpledialog.ui;

public interface MvpPresenter<V extends MvpView> {

    void onAttachView(V mvpView);

    void onDetachView();
}
