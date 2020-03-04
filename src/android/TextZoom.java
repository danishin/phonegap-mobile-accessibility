/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/

package com.danishin.textzoom;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.webkit.WebView;

import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class provides information on the status of native accessibility services to JavaScript.
 */
public class TextZoom extends CordovaPlugin {
    private View mView;
    
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        
        try {
            Method getView = webView.getClass().getMethod("getView");
            mView = (View) getView.invoke(webView);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if(action.equals("getTextZoom")) {
                getTextZoom(callbackContext);
                return true;
            } else if(action.equals("setTextZoom")) {
                if (args.length() > 0) {
                    double textZoom = args.getDouble(0);
                    if (textZoom > 0) {
                        setTextZoom(textZoom, callbackContext);
                    }
                }
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
        }
        return false;
    }

    private void getTextZoom(final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                double textZoom = 100;
                try {
                    Method getSettings = mView.getClass().getMethod("getSettings");
                    Object wSettings = getSettings.invoke(mView);
                    Method getTextZoom = wSettings.getClass().getMethod("getTextZoom");
                    textZoom = Double.valueOf(getTextZoom.invoke(wSettings).toString());
                } catch (ClassCastException ce) {
                    ce.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                
                if (callbackContext != null) {
                    callbackContext.success((int) textZoom);
                }
            }
        });
    }

    private void setTextZoom(final double textZoom, final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    Method getSettings = mView.getClass().getMethod("getSettings");
                    Object wSettings = getSettings.invoke(mView);
                    Method setTextZoom = wSettings.getClass().getMethod("setTextZoom", Integer.TYPE);
                    setTextZoom.invoke(wSettings, (int) textZoom);
                } catch (ClassCastException ce) {
                    ce.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                
                if (callbackContext != null) {
                    callbackContext.success((int) this.getTextZoom());
                }
            }
        });
    }

    public void setTextZoom(final double textZoom) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                this.setTextZoom(textZoom);
            }
        });
    }
}
