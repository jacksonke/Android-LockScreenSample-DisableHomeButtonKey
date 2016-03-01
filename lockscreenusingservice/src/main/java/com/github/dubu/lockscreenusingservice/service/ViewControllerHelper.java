package com.github.dubu.lockscreenusingservice.service;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/2/23.
 */
public class ViewControllerHelper {
    public interface ControllerHelperListener{
        public void onControllerChanged(ViewControllerBase viewController);
    }

    private ViewControllerBase mCurrentController = null;
    private static ViewControllerHelper sFactory = null;
    private WeakReference<ControllerHelperListener> mListenerRef = null;
//    private ArrayMap<String, Class<?> > mMap;

    public void setListener(ControllerHelperListener listener){
        mListenerRef = new WeakReference<>(listener);
    }


    public static ViewControllerHelper getDefaultInstance(){
        if (sFactory == null){
            sFactory = new ViewControllerHelper();
        }

        return  sFactory;
    }

//    public void registerViewController(ViewControllerBase viewController){
//        if (viewController == null){
//            return;
//        }
//
//        if (mMap == null){
//            mMap = new ArrayMap<>();
//        }
//
//        mMap.put(viewController.getClass().getName(), viewController.getClass());
//    }
//
//    public ViewControllerBase getViewController(String key){
//        do {
//            if (key == null){
//                break;
//            }
//
//            if (mMap == null){
//                break;
//            }
//
//            Class<?> clz = mMap.get(key);
//            if (clz == null){
//                break;
//            }
//
//            try {
//                Method method = clz.getMethod("getInstance", clz);
//
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            }
//        } while (false);
//
//    }

    public void setViewController(ViewControllerBase viewController){
        if (mCurrentController != viewController){
            mCurrentController = viewController;

            if (mListenerRef != null){
                ControllerHelperListener listener = mListenerRef.get();
                if (listener != null){
                    listener.onControllerChanged(viewController);
                }
            }
        }

    }

    public ViewControllerBase getCurrentVewController(){
        return mCurrentController != null ? mCurrentController:new LockscreenViewController();
    }

}
