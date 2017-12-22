package com.example.brona.picasaconnect;

import java.util.List;

public interface PhotosView {

    interface View extends BaseView<Presenter> {
        void setLoadingIndicator(boolean active);
        void showData(List<Photo> photoList);
        void showLoadError(String msg);
        void showPhotoUI(int albumIndex);
    }

    interface Presenter extends BasePresenter {
        void requestData(ParcelableAlbum album);
        void onDataSelected(int index);
    }
}
