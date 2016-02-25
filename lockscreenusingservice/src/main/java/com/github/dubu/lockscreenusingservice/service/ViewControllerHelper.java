package com.github.dubu.lockscreenusingservice.service;

/**
 * Created by Administrator on 2016/2/23.
 */
public class ViewControllerHelper {
    private ViewControllerBase mCurrentController = null;

    private static ViewControllerHelper sFactory = null;
//    private ArrayMap<String, Class<?> > mMap;

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
        mCurrentController = viewController;
    }

    public ViewControllerBase getCurrentVewController(){
        return mCurrentController != null ? mCurrentController:new LockscreenViewController();
    }

}
