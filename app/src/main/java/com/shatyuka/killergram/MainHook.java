package com.shatyuka.killergram;

import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    public final static List<String> hookPackages = Arrays.asList(
            "org.telegram.messenger",
            "org.telegram.messenger.web",
            "org.telegram.messenger.beta",
            "tw.nekomimi.nekogram",
            "nekox.messenger",
            "com.cool2645.nekolite",
            "org.telegram.plus",
            "com.iMe.android",
            "org.telegram.BifToGram",
            "ua.itaysonlab.messenger",
            "org.forkclient.messenger",
            "org.forkclient.messenger.beta",
            "org.aka.messenger",
            "ellipi.messenger",
            "org.nift4.catox",
            "it.owlgram.android",
            "com.exteragram.messenger");

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (hookPackages.contains(lpparam.packageName)) {
            try {
                // Working with MessageController - sponsored messages and forwarding
                Class<?> messagesControllerClass = XposedHelpers.findClassIfExists("org.telegram.messenger.MessagesController", lpparam.classLoader);
                if (messagesControllerClass != null) {
                    XposedBridge.hookAllMethods(messagesControllerClass, "getSponsoredMessages", XC_MethodReplacement.returnConstant(null));
                    XposedBridge.hookAllMethods(messagesControllerClass, "isChatNoForwards", XC_MethodReplacement.returnConstant(false));
                } else {
                    XposedBridge.log("[Killergram] messagesControllerClass not found");
                }

                // Working with ChatActivity - sponsored messages
                Class<?> chatUIActivityClass = XposedHelpers.findClassIfExists("org.telegram.ui.ChatActivity", lpparam.classLoader);
                if (chatUIActivityClass != null) {
                    XposedBridge.hookAllMethods(chatUIActivityClass, "addSponsoredMessages", XC_MethodReplacement.returnConstant(null));
                } else {
                    XposedBridge.log("[Killergram] chatUIActivityClass not found");
                }

                // Working with SharedConfig - performance class
                Class<?> SharedConfigClass = XposedHelpers.findClassIfExists("org.telegram.messenger.SharedConfig", lpparam.classLoader);
                if (SharedConfigClass != null) {
                    XposedBridge.hookAllMethods(SharedConfigClass, "getDevicePerformanceClass", XC_MethodReplacement.returnConstant(2));
                } else {
                    XposedBridge.log("[Killergram] SharedConfigClass not found");
                }

                // Working with UserConfig - acc count
                Class<?> UserConfigClass = XposedHelpers.findClassIfExists("org.telegram.messenger.UserConfig", lpparam.classLoader);
                if (UserConfigClass != null) {
                    XposedBridge.hookAllMethods(UserConfigClass, "getMaxAccountCount", XC_MethodReplacement.returnConstant(999));
                    XposedBridge.hookAllMethods(UserConfigClass, "hasPremiumOnAccounts", XC_MethodReplacement.returnConstant(true));
                    // XposedBridge.hookAllMethods(UserConfigClass, "isPremium", XC_MethodReplacement.returnConstant(true));  // Planning to do a file existence check or some other way to enable it afterwards
                } else {
                    XposedBridge.log("[Killergram] UserConfigClass not found");
                }

                // Working with TranslateController - auto translate
                Class<?> TranslateControllerClass = XposedHelpers.findClassIfExists("org.telegram.messenger.TranslateController", lpparam.classLoader);
                if (TranslateControllerClass != null) {
                    XposedBridge.hookAllMethods(TranslateControllerClass, "isFeatureAvailable", XC_MethodReplacement.returnConstant(true));
                } else {
                    XposedBridge.log("[Killergram] TranslateControllerClass not found");
                }


                XposedBridge.log("[Killergram] Hook success for " + lpparam.packageName);
            } catch (Throwable error) {
                XposedBridge.log("[Killergram] Hook failure for " + lpparam.packageName);
                XposedBridge.log(Arrays.toString(error.getStackTrace()));
            }
        }
    }
}
