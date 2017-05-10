package ar.nerd.detector;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class detector extends Activity implements B4AActivity{
	public static detector mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "ar.nerd.detector", "ar.nerd.detector.detector");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (detector).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "ar.nerd.detector", "ar.nerd.detector.detector");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "ar.nerd.detector.detector", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (detector) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (detector) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return detector.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (detector) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (detector) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        Object[] o;
        if (permissions.length > 0)
            o = new Object[] {permissions[0], grantResults[0] == 0};
        else
            o = new Object[] {"", false};
        processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.phone.Phone.PhoneSensors _vvv1 = null;
public static anywheresoftware.b4a.objects.Timer _vvv2 = null;
public static anywheresoftware.b4a.objects.Timer _vvv3 = null;
public static float _vvvvvvvv2 = 0f;
public anywheresoftware.b4a.objects.PanelWrapper _vvvvvvv7 = null;
public anywheresoftware.b4a.objects.PanelWrapper _vvvvvvv0 = null;
public anywheresoftware.b4a.objects.PanelWrapper _vvvvvvvv1 = null;
public anywheresoftware.b4a.objects.PanelWrapper _vvvvvvvv3 = null;
public anywheresoftware.b4a.objects.PanelWrapper _vvvvvvvv4 = null;
public static float _vvvvvvvvvv2 = 0f;
public anywheresoftware.b4a.audio.Beeper _vvvvv5 = null;
public anywheresoftware.b4a.phone.Phone.PhoneWakeState _vvvvvv1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _vvvvvvv4 = null;
public anywheresoftware.b4a.objects.LabelWrapper _vvvvvvv2 = null;
public anywheresoftware.b4a.objects.PanelWrapper _vvvvvvvv5 = null;
public static String _vvvvvv5 = "";
public static String _vvvvvvv3 = "";
public static String _vvvvvvv5 = "";
public static String _vvvvvvvvvv3 = "";
public static String _vvvvvvvvvv4 = "";
public static String _vvvvvvvvvv5 = "";
public static String _vvvvvvvvvv6 = "";
public static String _vvvvvv7 = "";
public static String _vvvvvv6 = "";
public static String _vvvvvv0 = "";
public static String _vvvvvvv1 = "";
public static String _vvvvvvvvv4 = "";
public static String _vvvvvvvvv7 = "";
public static String _vvvvvvvvv2 = "";
public static String _vvvvvvvvvv7 = "";
public anywheresoftware.b4a.objects.ButtonWrapper _vvvvvvvv7 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _vvvvvvvv6 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _vvvvvvvv0 = null;
public anywheresoftware.b4a.objects.PanelWrapper _vvvvvv4 = null;
public anywheresoftware.b4a.objects.PanelWrapper _vvvvvv2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _vvvvvvvvv1 = null;
public anywheresoftware.b4a.objects.PanelWrapper _vvvvvv3 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _vvvvvvvvv5 = null;
public static boolean _vvvvv0 = false;
public anywheresoftware.b4a.objects.PanelWrapper _vvvvvvvvv3 = null;
public anywheresoftware.b4a.objects.PanelWrapper _vvvvvvvvv6 = null;
public anywheresoftware.b4a.objects.PanelWrapper _vvvvvvvvv0 = null;
public anywheresoftware.b4a.objects.Timer _vvvvv6 = null;
public static int _vvvvvvvvvv0 = 0;
public static String _vvvvv2 = "";
public anywheresoftware.b4a.samples.httputils2.httputils2service _vvvv1 = null;
public ar.nerd.detector.main _vvvv0 = null;
public ar.nerd.detector.statemanager _vvvv2 = null;
public ar.nerd.detector.starter _vvvv3 = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 81;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 83;BA.debugLine="mostrarHelp=StateManager.GetSetting (\"mostrarhelp";
mostCurrent._vvvvv2 = mostCurrent._vvvv2._vv2(mostCurrent.activityBA,"mostrarhelp");
 //BA.debugLineNum = 85;BA.debugLine="If mostrarHelp <> \"no\" Then";
if ((mostCurrent._vvvvv2).equals("no") == false) { 
 //BA.debugLineNum = 86;BA.debugLine="StateManager.SetSetting(\"mostrarhelp\", \"no\" )";
mostCurrent._vvvv2._vv0(mostCurrent.activityBA,"mostrarhelp","no");
 //BA.debugLineNum = 87;BA.debugLine="StateManager.SaveSettings";
mostCurrent._vvvv2._vv6(mostCurrent.activityBA);
 };
 //BA.debugLineNum = 92;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 93;BA.debugLine="ps.Initialize(ps.TYPE_ACCELEROMETER)";
_vvv1.Initialize(_vvv1.TYPE_ACCELEROMETER);
 //BA.debugLineNum = 95;BA.debugLine="setearLenguaje";
_vvvvv3();
 //BA.debugLineNum = 96;BA.debugLine="dibujar";
_vvvvv4();
 //BA.debugLineNum = 98;BA.debugLine="bp.Initialize2(100,2500,bp.VOLUME_MUSIC)";
mostCurrent._vvvvv5.Initialize2((int) (100),(int) (2500),mostCurrent._vvvvv5.VOLUME_MUSIC);
 //BA.debugLineNum = 99;BA.debugLine="timer1.Initialize(\"Timer1\", 1000)";
_vvv2.Initialize(processBA,"Timer1",(long) (1000));
 //BA.debugLineNum = 101;BA.debugLine="timer2.Initialize(\"Timer2\", 100)";
_vvv3.Initialize(processBA,"Timer2",(long) (100));
 //BA.debugLineNum = 103;BA.debugLine="timer3.Initialize(\"timer3\", 500)";
mostCurrent._vvvvv6.Initialize(processBA,"timer3",(long) (500));
 //BA.debugLineNum = 104;BA.debugLine="timer3.Enabled=True";
mostCurrent._vvvvv6.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 105;BA.debugLine="enviarping";
_vvvvv7();
 };
 //BA.debugLineNum = 108;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 194;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 196;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 //BA.debugLineNum = 203;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 184;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 189;BA.debugLine="If start= True Then timer1.Enabled = True";
if (_vvvvv0==anywheresoftware.b4a.keywords.Common.True) { 
_vvv2.setEnabled(anywheresoftware.b4a.keywords.Common.True);};
 //BA.debugLineNum = 191;BA.debugLine="pw.KeepAlive(True)";
mostCurrent._vvvvvv1.KeepAlive(processBA,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 192;BA.debugLine="End Sub";
return "";
}
public static String  _animation_animationend() throws Exception{
flm.b4a.animationplus.AnimationPlusWrapper _anim = null;
 //BA.debugLineNum = 611;BA.debugLine="Sub animation_AnimationEnd";
 //BA.debugLineNum = 614;BA.debugLine="panelHelp.Left=-Activity.Width";
mostCurrent._vvvvvv2.setLeft((int) (-mostCurrent._activity.getWidth()));
 //BA.debugLineNum = 615;BA.debugLine="Dim anim As AnimationPlus";
_anim = new flm.b4a.animationplus.AnimationPlusWrapper();
 //BA.debugLineNum = 616;BA.debugLine="anim.InitializeTranslate(\"animation1\",0,0,-Activi";
_anim.InitializeTranslate(mostCurrent.activityBA,"animation1",(float) (0),(float) (0),(float) (-mostCurrent._activity.getWidth()),(float) (0));
 //BA.debugLineNum = 617;BA.debugLine="anim.Duration=100";
_anim.setDuration((long) (100));
 //BA.debugLineNum = 618;BA.debugLine="anim.PersistAfter=False";
_anim.setPersistAfter(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 619;BA.debugLine="anim.Start(panelHelp2)";
_anim.Start((android.view.View)(mostCurrent._vvvvvv3.getObject()));
 //BA.debugLineNum = 621;BA.debugLine="End Sub";
return "";
}
public static String  _animation1_animationend() throws Exception{
 //BA.debugLineNum = 623;BA.debugLine="Sub animation1_AnimationEnd";
 //BA.debugLineNum = 625;BA.debugLine="panelHelp2.left=0";
mostCurrent._vvvvvv3.setLeft((int) (0));
 //BA.debugLineNum = 626;BA.debugLine="End Sub";
return "";
}
public static String  _animation2_animationend() throws Exception{
 //BA.debugLineNum = 638;BA.debugLine="Sub animation2_AnimationEnd";
 //BA.debugLineNum = 639;BA.debugLine="panelHelp2.Enabled=False";
mostCurrent._vvvvvv3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 640;BA.debugLine="panelHelp2.left=-Activity.Width";
mostCurrent._vvvvvv3.setLeft((int) (-mostCurrent._activity.getWidth()));
 //BA.debugLineNum = 644;BA.debugLine="End Sub";
return "";
}
public static String  _botonabout_longclick() throws Exception{
 //BA.debugLineNum = 583;BA.debugLine="Sub botonAbout_LongClick";
 //BA.debugLineNum = 584;BA.debugLine="panelAbout.Visible=True";
mostCurrent._vvvvvv4.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 585;BA.debugLine="End Sub";
return "";
}
public static String  _botonconfig_click() throws Exception{
anywheresoftware.b4a.phone.Phone _p = null;
anywheresoftware.b4a.agraham.dialogs.InputDialog _dialogo = null;
int _accion = 0;
 //BA.debugLineNum = 525;BA.debugLine="Sub botonConfig_Click";
 //BA.debugLineNum = 526;BA.debugLine="Dim p As Phone";
_p = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 527;BA.debugLine="Dim dialogo As InputDialog";
_dialogo = new anywheresoftware.b4a.agraham.dialogs.InputDialog();
 //BA.debugLineNum = 528;BA.debugLine="Dim accion As Int";
_accion = 0;
 //BA.debugLineNum = 530;BA.debugLine="If start=True Then";
if (_vvvvv0==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 531;BA.debugLine="timer1.Enabled = False";
_vvv2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 532;BA.debugLine="ps.StopListening";
_vvv1.StopListening(processBA);
 };
 //BA.debugLineNum = 535;BA.debugLine="dialogo.Input=texto";
_dialogo.setInput(mostCurrent._vvvvvv5);
 //BA.debugLineNum = 536;BA.debugLine="accion=dialogo.Show(descripcionconfig,tituloconfi";
_accion = _dialogo.Show(mostCurrent._vvvvvv6,mostCurrent._vvvvvv7,mostCurrent._vvvvvv0,mostCurrent._vvvvvvv1,"",mostCurrent.activityBA,(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 539;BA.debugLine="If accion = DialogResponse.POSITIVE Then";
if (_accion==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 540;BA.debugLine="texto=dialogo.Input";
mostCurrent._vvvvvv5 = _dialogo.getInput();
 //BA.debugLineNum = 541;BA.debugLine="StateManager.SetSetting(\"detectar\", texto )";
mostCurrent._vvvv2._vv0(mostCurrent.activityBA,"detectar",mostCurrent._vvvvvv5);
 //BA.debugLineNum = 542;BA.debugLine="StateManager.SaveSettings";
mostCurrent._vvvv2._vv6(mostCurrent.activityBA);
 //BA.debugLineNum = 543;BA.debugLine="setearLenguaje";
_vvvvv3();
 //BA.debugLineNum = 544;BA.debugLine="display.Text=textodetector";
mostCurrent._vvvvvvv2.setText((Object)(mostCurrent._vvvvvvv3));
 //BA.debugLineNum = 545;BA.debugLine="etiqueta.Text=textomedidor";
mostCurrent._vvvvvvv4.setText((Object)(mostCurrent._vvvvvvv5));
 };
 //BA.debugLineNum = 548;BA.debugLine="p.HideKeyboard(Activity)";
_p.HideKeyboard(mostCurrent._activity);
 //BA.debugLineNum = 550;BA.debugLine="If start=True Then";
if (_vvvvv0==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 551;BA.debugLine="timer1.Enabled=True";
_vvv2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 552;BA.debugLine="ps.StartListening(\"Sensor\")";
_vvv1.StartListening(processBA,"Sensor");
 };
 //BA.debugLineNum = 555;BA.debugLine="End Sub";
return "";
}
public static String  _botonnexthelp_click() throws Exception{
flm.b4a.animationplus.AnimationPlusWrapper _anim = null;
 //BA.debugLineNum = 600;BA.debugLine="Sub botonNexthelp_Click";
 //BA.debugLineNum = 603;BA.debugLine="Dim anim As AnimationPlus";
_anim = new flm.b4a.animationplus.AnimationPlusWrapper();
 //BA.debugLineNum = 604;BA.debugLine="anim.InitializeTranslate(\"animation\",0,0,-Activit";
_anim.InitializeTranslate(mostCurrent.activityBA,"animation",(float) (0),(float) (0),(float) (-mostCurrent._activity.getWidth()),(float) (0));
 //BA.debugLineNum = 605;BA.debugLine="anim.Duration=200";
_anim.setDuration((long) (200));
 //BA.debugLineNum = 606;BA.debugLine="anim.PersistAfter=False";
_anim.setPersistAfter(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 607;BA.debugLine="anim.Start(panelHelp)";
_anim.Start((android.view.View)(mostCurrent._vvvvvv2.getObject()));
 //BA.debugLineNum = 609;BA.debugLine="End Sub";
return "";
}
public static String  _botonokhelp2_click() throws Exception{
flm.b4a.animationplus.AnimationPlusWrapper _anim = null;
 //BA.debugLineNum = 628;BA.debugLine="Sub botonOKhelp2_Click";
 //BA.debugLineNum = 630;BA.debugLine="Dim anim As AnimationPlus";
_anim = new flm.b4a.animationplus.AnimationPlusWrapper();
 //BA.debugLineNum = 631;BA.debugLine="anim.InitializeTranslate(\"animation2\",0,0,-Activi";
_anim.InitializeTranslate(mostCurrent.activityBA,"animation2",(float) (0),(float) (0),(float) (-mostCurrent._activity.getWidth()),(float) (0));
 //BA.debugLineNum = 632;BA.debugLine="anim.Duration=100";
_anim.setDuration((long) (100));
 //BA.debugLineNum = 633;BA.debugLine="anim.PersistAfter=True";
_anim.setPersistAfter(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 634;BA.debugLine="anim.Start(panelHelp2)";
_anim.Start((android.view.View)(mostCurrent._vvvvvv3.getObject()));
 //BA.debugLineNum = 635;BA.debugLine="End Sub";
return "";
}
public static String  _botonstart_click() throws Exception{
 //BA.debugLineNum = 561;BA.debugLine="Sub botonStart_Click";
 //BA.debugLineNum = 563;BA.debugLine="If start= False Then";
if (_vvvvv0==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 564;BA.debugLine="timer1.Enabled = True";
_vvv2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 565;BA.debugLine="ps.StartListening(\"Sensor\")";
_vvv1.StartListening(processBA,"Sensor");
 //BA.debugLineNum = 566;BA.debugLine="start=True";
_vvvvv0 = anywheresoftware.b4a.keywords.Common.True;
 }else {
 //BA.debugLineNum = 568;BA.debugLine="timer1.Enabled = False";
_vvv2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 569;BA.debugLine="ps.StopListening";
_vvv1.StopListening(processBA);
 //BA.debugLineNum = 570;BA.debugLine="start=False";
_vvvvv0 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 572;BA.debugLine="rotarCentro(-47,aguja)";
_vvvvvvv6((float) (-47),(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._vvvvvvv7.getObject())));
 };
 //BA.debugLineNum = 581;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvv4() throws Exception{
float _ancho = 0f;
float _pivotx = 0f;
float _largoaguja = 0f;
float _largototalaguja = 0f;
float _escala = 0f;
float _anchoetiqueta = 0f;
float _altotiqueta = 0f;
anywheresoftware.b4a.agraham.reflection.Reflection _obj1 = null;
float _anchotornillo = 0f;
float _posxtornillo = 0f;
float _posytornillo = 0f;
float _ancholuz = 0f;
anywheresoftware.b4a.keywords.constants.TypefaceWrapper _font = null;
float _anchodisplay = 0f;
float _altodisplay = 0f;
float _posydisplay = 0f;
float _anchoboton = 0f;
float _altoboton = 0f;
String _anchomano = "";
anywheresoftware.b4a.objects.LabelWrapper _labelhelp1 = null;
anywheresoftware.b4a.objects.LabelWrapper _labelhelp2 = null;
 //BA.debugLineNum = 294;BA.debugLine="Sub dibujar";
 //BA.debugLineNum = 296;BA.debugLine="Dim ancho As Float= Activity.width";
_ancho = (float) (mostCurrent._activity.getWidth());
 //BA.debugLineNum = 297;BA.debugLine="Dim pivotx As Float";
_pivotx = 0f;
 //BA.debugLineNum = 298;BA.debugLine="Dim largoAguja As Float =ancho *0.35";
_largoaguja = (float) (_ancho*0.35);
 //BA.debugLineNum = 299;BA.debugLine="Dim largoTotalaguja As Float =largoAguja * 2";
_largototalaguja = (float) (_largoaguja*2);
 //BA.debugLineNum = 304;BA.debugLine="Dim escala As Float = GetDeviceLayoutValues.App";
_escala = (float) (anywheresoftware.b4a.keywords.Common.GetDeviceLayoutValues(mostCurrent.activityBA).getApproximateScreenSize()/(double)4.5);
 //BA.debugLineNum = 307;BA.debugLine="fondo.Initialize(\"\")";
mostCurrent._vvvvvvv0.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 308;BA.debugLine="fondo.SetBackgroundImage(LoadBitmap (File.DirAsse";
mostCurrent._vvvvvvv0.SetBackgroundImage((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"fondo.png").getObject()));
 //BA.debugLineNum = 309;BA.debugLine="Activity.AddView(fondo,0, 0,ancho,Activity.Height";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvv0.getObject()),(int) (0),(int) (0),(int) (_ancho),mostCurrent._activity.getHeight());
 //BA.debugLineNum = 313;BA.debugLine="cara.Initialize(\"\")";
mostCurrent._vvvvvvvv1.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 314;BA.debugLine="cara.SetBackgroundImage(LoadBitmap (File.DirAsset";
mostCurrent._vvvvvvvv1.SetBackgroundImage((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"cara.png").getObject()));
 //BA.debugLineNum = 315;BA.debugLine="Activity.AddView(cara,0, 20dip,ancho,ancho)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvvv1.getObject()),(int) (0),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20)),(int) (_ancho),(int) (_ancho));
 //BA.debugLineNum = 320;BA.debugLine="Dim anchoetiqueta As Float=ancho*0.30";
_anchoetiqueta = (float) (_ancho*0.30);
 //BA.debugLineNum = 321;BA.debugLine="Dim altotiqueta As Float=ancho*0.1";
_altotiqueta = (float) (_ancho*0.1);
 //BA.debugLineNum = 322;BA.debugLine="etiqueta.Initialize(\"\")";
mostCurrent._vvvvvvv4.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 324;BA.debugLine="Activity.AddView(etiqueta, (Activity.Width/2)-(an";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvv4.getObject()),(int) ((mostCurrent._activity.getWidth()/(double)2)-(_anchoetiqueta/(double)2)),(int) (_ancho*0.47),(int) (_anchoetiqueta),(int) (_altotiqueta));
 //BA.debugLineNum = 325;BA.debugLine="etiqueta.Gravity=Gravity.CENTER";
mostCurrent._vvvvvvv4.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 326;BA.debugLine="etiqueta.TextColor=Colors.ARGB(225,0,0,0)";
mostCurrent._vvvvvvv4.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (225),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 329;BA.debugLine="Dim Obj1 As Reflector";
_obj1 = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 330;BA.debugLine="Obj1.Target = etiqueta";
_obj1.Target = (Object)(mostCurrent._vvvvvvv4.getObject());
 //BA.debugLineNum = 331;BA.debugLine="Obj1.RunMethod3(\"setLineSpacing\", 1, \"java.lan";
_obj1.RunMethod3("setLineSpacing",BA.NumberToString(1),"java.lang.float",BA.NumberToString(0.7),"java.lang.float");
 //BA.debugLineNum = 336;BA.debugLine="etiqueta.Text=textomedidor";
mostCurrent._vvvvvvv4.setText((Object)(mostCurrent._vvvvvvv5));
 //BA.debugLineNum = 337;BA.debugLine="etiqueta.TextSize=14";
mostCurrent._vvvvvvv4.setTextSize((float) (14));
 //BA.debugLineNum = 341;BA.debugLine="pivotx= 22dip+((ancho *0.7)-largoAguja)";
_pivotx = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (22))+((_ancho*0.7)-_largoaguja));
 //BA.debugLineNum = 346;BA.debugLine="aguja.Initialize(\"\")";
mostCurrent._vvvvvvv7.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 347;BA.debugLine="aguja.SetBackgroundImage(LoadBitmap (File.DirAss";
mostCurrent._vvvvvvv7.SetBackgroundImage((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"aguja.png").getObject()));
 //BA.debugLineNum = 348;BA.debugLine="Activity.AddView(aguja,Activity.Width/2, pivo";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvv7.getObject()),(int) (mostCurrent._activity.getWidth()/(double)2),(int) (_pivotx),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),(int) (_largototalaguja));
 //BA.debugLineNum = 350;BA.debugLine="aguja.Left=1dip+(Activity.Width-aguja.Width)/2";
mostCurrent._vvvvvvv7.setLeft((int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))+(mostCurrent._activity.getWidth()-mostCurrent._vvvvvvv7.getWidth())/(double)2));
 //BA.debugLineNum = 352;BA.debugLine="rotacion=-47";
_vvvvvvvv2 = (float) (-47);
 //BA.debugLineNum = 353;BA.debugLine="rotarCentro(rotacion,aguja)";
_vvvvvvv6(_vvvvvvvv2,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._vvvvvvv7.getObject())));
 //BA.debugLineNum = 357;BA.debugLine="tornillo.Initialize(\"\")";
mostCurrent._vvvvvvvv3.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 358;BA.debugLine="Dim anchotornillo As Float= ancho * 0.05944056";
_anchotornillo = (float) (_ancho*0.05944056);
 //BA.debugLineNum = 359;BA.debugLine="Dim posxTornillo As Float=(ancho/2)- (anchotornil";
_posxtornillo = (float) ((_ancho/(double)2)-(_anchotornillo/(double)2));
 //BA.debugLineNum = 360;BA.debugLine="Dim posyTornillo As Float=20dip+ancho*0.6870629";
_posytornillo = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))+_ancho*0.6870629);
 //BA.debugLineNum = 362;BA.debugLine="tornillo.SetBackgroundImage(LoadBitmap (File.DirA";
mostCurrent._vvvvvvvv3.SetBackgroundImage((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"tornillo.png").getObject()));
 //BA.debugLineNum = 363;BA.debugLine="Activity.AddView(tornillo,posxTornillo, posyTorni";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvvv3.getObject()),(int) (_posxtornillo),(int) (_posytornillo),(int) (_anchotornillo),(int) (_anchotornillo));
 //BA.debugLineNum = 366;BA.debugLine="Dim anchoLuz As Float=80dip";
_ancholuz = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (80)));
 //BA.debugLineNum = 367;BA.debugLine="luz.Initialize(\"\")";
mostCurrent._vvvvvvvv4.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 369;BA.debugLine="luz.SetBackgroundImage(LoadBitmap (File.DirAssets";
mostCurrent._vvvvvvvv4.SetBackgroundImage((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"off.png").getObject()));
 //BA.debugLineNum = 370;BA.debugLine="Activity.AddView(luz,(Activity.Width/2)-(anchoLuz";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvvv4.getObject()),(int) ((mostCurrent._activity.getWidth()/(double)2)-(_ancholuz/(double)2)),(int) (_ancho+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),(int) (_ancholuz),(int) (_ancholuz));
 //BA.debugLineNum = 374;BA.debugLine="placa.Initialize(\"\")";
mostCurrent._vvvvvvvv5.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 375;BA.debugLine="placa.SetBackgroundImage(LoadBitmap (File.DirAsse";
mostCurrent._vvvvvvvv5.SetBackgroundImage((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"placa.png").getObject()));
 //BA.debugLineNum = 379;BA.debugLine="Dim font As Typeface";
_font = new anywheresoftware.b4a.keywords.constants.TypefaceWrapper();
 //BA.debugLineNum = 380;BA.debugLine="Dim anchodisplay As Float=ancho*0.75";
_anchodisplay = (float) (_ancho*0.75);
 //BA.debugLineNum = 381;BA.debugLine="Dim altodisplay As Float=anchodisplay*0.42";
_altodisplay = (float) (_anchodisplay*0.42);
 //BA.debugLineNum = 382;BA.debugLine="Dim posyDisplay As Float=(luz.Top+luz.Height)+10d";
_posydisplay = (float) ((mostCurrent._vvvvvvvv4.getTop()+mostCurrent._vvvvvvvv4.getHeight())+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)));
 //BA.debugLineNum = 384;BA.debugLine="font=Typeface.LoadFromAssets(\"helvetica.ttf\")";
_font.setObject((android.graphics.Typeface)(anywheresoftware.b4a.keywords.Common.Typeface.LoadFromAssets("helvetica.ttf")));
 //BA.debugLineNum = 386;BA.debugLine="display.Initialize(\"\")";
mostCurrent._vvvvvvv2.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 389;BA.debugLine="Activity.AddView(placa, (Activity.Width/2)-(an";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvvv5.getObject()),(int) ((mostCurrent._activity.getWidth()/(double)2)-(_anchodisplay/(double)2)),(int) (_posydisplay),(int) (_anchodisplay),(int) (_altodisplay));
 //BA.debugLineNum = 391;BA.debugLine="anchodisplay=anchodisplay*0.9";
_anchodisplay = (float) (_anchodisplay*0.9);
 //BA.debugLineNum = 392;BA.debugLine="Activity.AddView(display, (Activity.Width/2)-(anc";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvv2.getObject()),(int) ((mostCurrent._activity.getWidth()/(double)2)-(_anchodisplay/(double)2)),(int) (_posydisplay),(int) (_anchodisplay),(int) (_altodisplay));
 //BA.debugLineNum = 393;BA.debugLine="display.Gravity=Gravity.CENTER";
mostCurrent._vvvvvvv2.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 394;BA.debugLine="display.TextColor=Colors.ARGB(255,136,106,80)";
mostCurrent._vvvvvvv2.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (136),(int) (106),(int) (80)));
 //BA.debugLineNum = 395;BA.debugLine="display.Typeface=font";
mostCurrent._vvvvvvv2.setTypeface((android.graphics.Typeface)(_font.getObject()));
 //BA.debugLineNum = 399;BA.debugLine="Dim Obj1 As Reflector";
_obj1 = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 400;BA.debugLine="Obj1.Target = display";
_obj1.Target = (Object)(mostCurrent._vvvvvvv2.getObject());
 //BA.debugLineNum = 401;BA.debugLine="Obj1.RunMethod3(\"setLineSpacing\", 1, \"java.lan";
_obj1.RunMethod3("setLineSpacing",BA.NumberToString(1),"java.lang.float",BA.NumberToString(0.9),"java.lang.float");
 //BA.debugLineNum = 406;BA.debugLine="display.Text=textodetector";
mostCurrent._vvvvvvv2.setText((Object)(mostCurrent._vvvvvvv3));
 //BA.debugLineNum = 407;BA.debugLine="display.TextSize=30";
mostCurrent._vvvvvvv2.setTextSize((float) (30));
 //BA.debugLineNum = 413;BA.debugLine="botonConfig.Initialize(\"botonConfig\")";
mostCurrent._vvvvvvvv6.Initialize(mostCurrent.activityBA,"botonConfig");
 //BA.debugLineNum = 414;BA.debugLine="botonStart.Initialize(\"botonStart\")";
mostCurrent._vvvvvvvv7.Initialize(mostCurrent.activityBA,"botonStart");
 //BA.debugLineNum = 415;BA.debugLine="botonAbout.Initialize(\"botonAbout\")";
mostCurrent._vvvvvvvv0.Initialize(mostCurrent.activityBA,"botonAbout");
 //BA.debugLineNum = 418;BA.debugLine="Activity.AddView(botonConfig,0, posyDisplay,ancho";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvvv6.getObject()),(int) (0),(int) (_posydisplay),(int) (_ancho),(int) (_altodisplay));
 //BA.debugLineNum = 422;BA.debugLine="Activity.AddView(botonStart,0, ancho+20dip,ancho,";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvvv7.getObject()),(int) (0),(int) (_ancho+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),(int) (_ancho),(int) (_ancholuz));
 //BA.debugLineNum = 425;BA.debugLine="Activity.AddView(botonAbout,0, 0,ancho,ancho)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvvv0.getObject()),(int) (0),(int) (0),(int) (_ancho),(int) (_ancho));
 //BA.debugLineNum = 427;BA.debugLine="botonConfig.Color=Colors.Transparent";
mostCurrent._vvvvvvvv6.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 428;BA.debugLine="botonStart.Color=Colors.Transparent";
mostCurrent._vvvvvvvv7.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 429;BA.debugLine="botonAbout.Color=Colors.Transparent";
mostCurrent._vvvvvvvv0.setColor(anywheresoftware.b4a.keywords.Common.Colors.Transparent);
 //BA.debugLineNum = 436;BA.debugLine="panelAbout.Initialize(\"panelAbout\")";
mostCurrent._vvvvvv4.Initialize(mostCurrent.activityBA,"panelAbout");
 //BA.debugLineNum = 437;BA.debugLine="panelAbout.SetBackgroundImage(LoadBitmap (File.D";
mostCurrent._vvvvvv4.SetBackgroundImage((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"about.png").getObject()));
 //BA.debugLineNum = 438;BA.debugLine="panelAbout.Visible=False";
mostCurrent._vvvvvv4.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 439;BA.debugLine="Activity.AddView(panelAbout,0, 0,Activity.width,";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvv4.getObject()),(int) (0),(int) (0),mostCurrent._activity.getWidth(),mostCurrent._activity.getHeight());
 //BA.debugLineNum = 448;BA.debugLine="If mostrarHelp <> \"no\"	Then";
if ((mostCurrent._vvvvv2).equals("no") == false) { 
 //BA.debugLineNum = 451;BA.debugLine="panelHelp.Initialize(\"panelHelp\")";
mostCurrent._vvvvvv2.Initialize(mostCurrent.activityBA,"panelHelp");
 //BA.debugLineNum = 452;BA.debugLine="panelHelp.Color=Colors.ARGB(180,0,0,0)";
mostCurrent._vvvvvv2.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (180),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 454;BA.debugLine="botonNexthelp.Initialize(\"botonNexthelp\")";
mostCurrent._vvvvvvvvv1.Initialize(mostCurrent.activityBA,"botonNexthelp");
 //BA.debugLineNum = 455;BA.debugLine="botonNexthelp.Color=Colors.ARGB(255,57,186,234)";
mostCurrent._vvvvvvvvv1.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (57),(int) (186),(int) (234)));
 //BA.debugLineNum = 456;BA.debugLine="botonNexthelp.Text=textoseguir";
mostCurrent._vvvvvvvvv1.setText((Object)(mostCurrent._vvvvvvvvv2));
 //BA.debugLineNum = 457;BA.debugLine="botonNexthelp.TextColor=Colors.White";
mostCurrent._vvvvvvvvv1.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 459;BA.debugLine="Dim anchoBoton As Float=150dip";
_anchoboton = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (150)));
 //BA.debugLineNum = 460;BA.debugLine="Dim altoboton As Float=40dip";
_altoboton = (float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (40)));
 //BA.debugLineNum = 461;BA.debugLine="Dim anchomano=Activity.Width/3";
_anchomano = BA.NumberToString(mostCurrent._activity.getWidth()/(double)3);
 //BA.debugLineNum = 462;BA.debugLine="panelHelp.AddView(botonNexthelp,ancho-anchoBoto";
mostCurrent._vvvvvv2.AddView((android.view.View)(mostCurrent._vvvvvvvvv1.getObject()),(int) (_ancho-_anchoboton-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),(int) (mostCurrent._activity.getHeight()-_altoboton-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),(int) (_anchoboton),(int) (_altoboton));
 //BA.debugLineNum = 464;BA.debugLine="Activity.AddView(panelHelp,0, 0,Activity.width,";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvv2.getObject()),(int) (0),(int) (0),mostCurrent._activity.getWidth(),mostCurrent._activity.getHeight());
 //BA.debugLineNum = 466;BA.debugLine="mano1.Initialize(\"\")";
mostCurrent._vvvvvvvvv3.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 467;BA.debugLine="mano1.SetBackgroundImage(LoadBitmap (File.DirAs";
mostCurrent._vvvvvvvvv3.SetBackgroundImage((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"mano2.png").getObject()));
 //BA.debugLineNum = 468;BA.debugLine="panelHelp.AddView(mano1,0,0,anchomano,anchomano";
mostCurrent._vvvvvv2.AddView((android.view.View)(mostCurrent._vvvvvvvvv3.getObject()),(int) (0),(int) (0),(int)(Double.parseDouble(_anchomano)),(int)(Double.parseDouble(_anchomano)));
 //BA.debugLineNum = 470;BA.debugLine="mano1.Left=(Activity.Width/2)-anchomano";
mostCurrent._vvvvvvvvv3.setLeft((int) ((mostCurrent._activity.getWidth()/(double)2)-(double)(Double.parseDouble(_anchomano))));
 //BA.debugLineNum = 471;BA.debugLine="mano1.Top=display.Top-(anchomano/2)";
mostCurrent._vvvvvvvvv3.setTop((int) (mostCurrent._vvvvvvv2.getTop()-((double)(Double.parseDouble(_anchomano))/(double)2)));
 //BA.debugLineNum = 473;BA.debugLine="Dim labelhelp1 As Label";
_labelhelp1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 474;BA.debugLine="labelhelp1.Initialize(\"\")";
_labelhelp1.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 475;BA.debugLine="labelhelp1.TextColor=Colors.ARGB(255,57,186,234";
_labelhelp1.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (57),(int) (186),(int) (234)));
 //BA.debugLineNum = 476;BA.debugLine="panelHelp.AddView(labelhelp1, 0,0,Activity.Widt";
mostCurrent._vvvvvv2.AddView((android.view.View)(_labelhelp1.getObject()),(int) (0),(int) (0),mostCurrent._activity.getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (70)));
 //BA.debugLineNum = 477;BA.debugLine="labelhelp1.Text=textohelp1";
_labelhelp1.setText((Object)(mostCurrent._vvvvvvvvv4));
 //BA.debugLineNum = 478;BA.debugLine="labelhelp1.TextSize=28";
_labelhelp1.setTextSize((float) (28));
 //BA.debugLineNum = 479;BA.debugLine="labelhelp1.Top=mano1.Top-labelhelp1.Height";
_labelhelp1.setTop((int) (mostCurrent._vvvvvvvvv3.getTop()-_labelhelp1.getHeight()));
 //BA.debugLineNum = 480;BA.debugLine="labelhelp1.Gravity=Bit.Or( Gravity.BOTTOM, Grav";
_labelhelp1.setGravity(anywheresoftware.b4a.keywords.Common.Bit.Or(anywheresoftware.b4a.keywords.Common.Gravity.BOTTOM,anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL));
 //BA.debugLineNum = 483;BA.debugLine="panelHelp2.Initialize(\"panelHelp2\")";
mostCurrent._vvvvvv3.Initialize(mostCurrent.activityBA,"panelHelp2");
 //BA.debugLineNum = 484;BA.debugLine="panelHelp2.Color=Colors.ARGB(180,0,0,0)";
mostCurrent._vvvvvv3.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (180),(int) (0),(int) (0),(int) (0)));
 //BA.debugLineNum = 486;BA.debugLine="botonOKhelp2.Initialize(\"botonOKhelp2\")";
mostCurrent._vvvvvvvvv5.Initialize(mostCurrent.activityBA,"botonOKhelp2");
 //BA.debugLineNum = 487;BA.debugLine="botonOKhelp2.Color=Colors.ARGB(255,57,186,234)";
mostCurrent._vvvvvvvvv5.setColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (57),(int) (186),(int) (234)));
 //BA.debugLineNum = 488;BA.debugLine="botonOKhelp2.Text=\"OK\"";
mostCurrent._vvvvvvvvv5.setText((Object)("OK"));
 //BA.debugLineNum = 489;BA.debugLine="botonOKhelp2.TextColor=Colors.White";
mostCurrent._vvvvvvvvv5.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 492;BA.debugLine="panelHelp2.AddView(botonOKhelp2,ancho-anchoBoto";
mostCurrent._vvvvvv3.AddView((android.view.View)(mostCurrent._vvvvvvvvv5.getObject()),(int) (_ancho-_anchoboton-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),(int) (mostCurrent._activity.getHeight()-_altoboton-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20))),(int) (_anchoboton),(int) (_altoboton));
 //BA.debugLineNum = 495;BA.debugLine="Activity.AddView(panelHelp2,Activity.width, 0,A";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvv3.getObject()),mostCurrent._activity.getWidth(),(int) (0),mostCurrent._activity.getWidth(),mostCurrent._activity.getHeight());
 //BA.debugLineNum = 498;BA.debugLine="mano2.Initialize(\"\")";
mostCurrent._vvvvvvvvv6.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 499;BA.debugLine="mano2.SetBackgroundImage(LoadBitmap (File.DirAs";
mostCurrent._vvvvvvvvv6.SetBackgroundImage((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"mano2.png").getObject()));
 //BA.debugLineNum = 500;BA.debugLine="panelHelp2.AddView(mano2,0,0,anchomano,anchoman";
mostCurrent._vvvvvv3.AddView((android.view.View)(mostCurrent._vvvvvvvvv6.getObject()),(int) (0),(int) (0),(int)(Double.parseDouble(_anchomano)),(int)(Double.parseDouble(_anchomano)));
 //BA.debugLineNum = 502;BA.debugLine="mano2.Left=(Activity.Width/2)-anchomano";
mostCurrent._vvvvvvvvv6.setLeft((int) ((mostCurrent._activity.getWidth()/(double)2)-(double)(Double.parseDouble(_anchomano))));
 //BA.debugLineNum = 503;BA.debugLine="mano2.Top=luz.Top-(anchomano/2)";
mostCurrent._vvvvvvvvv6.setTop((int) (mostCurrent._vvvvvvvv4.getTop()-((double)(Double.parseDouble(_anchomano))/(double)2)));
 //BA.debugLineNum = 505;BA.debugLine="Dim labelhelp2 As Label";
_labelhelp2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 506;BA.debugLine="labelhelp2.Initialize(\"\")";
_labelhelp2.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 507;BA.debugLine="labelhelp2.TextColor=Colors.ARGB(255,57,186,234";
_labelhelp2.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (255),(int) (57),(int) (186),(int) (234)));
 //BA.debugLineNum = 508;BA.debugLine="panelHelp2.AddView(labelhelp2, 0,0,Activity.Wid";
mostCurrent._vvvvvv3.AddView((android.view.View)(_labelhelp2.getObject()),(int) (0),(int) (0),mostCurrent._activity.getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (70)));
 //BA.debugLineNum = 509;BA.debugLine="labelhelp2.Text=textohelp2";
_labelhelp2.setText((Object)(mostCurrent._vvvvvvvvv7));
 //BA.debugLineNum = 510;BA.debugLine="labelhelp2.TextSize=28";
_labelhelp2.setTextSize((float) (28));
 //BA.debugLineNum = 511;BA.debugLine="labelhelp2.Top=mano2.Top-labelhelp2.Height";
_labelhelp2.setTop((int) (mostCurrent._vvvvvvvvv6.getTop()-_labelhelp2.getHeight()));
 //BA.debugLineNum = 512;BA.debugLine="labelhelp2.Gravity=Bit.Or( Gravity.BOTTOM, Grav";
_labelhelp2.setGravity(anywheresoftware.b4a.keywords.Common.Bit.Or(anywheresoftware.b4a.keywords.Common.Gravity.BOTTOM,anywheresoftware.b4a.keywords.Common.Gravity.CENTER_HORIZONTAL));
 };
 //BA.debugLineNum = 515;BA.debugLine="panelsplash.Initialize(\"\")";
mostCurrent._vvvvvvvvv0.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 516;BA.debugLine="panelsplash.SetBackgroundImage(LoadBitmap (File.";
mostCurrent._vvvvvvvvv0.SetBackgroundImage((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"splash.png").getObject()));
 //BA.debugLineNum = 517;BA.debugLine="Activity.AddView(panelsplash,0, 0,Activity.width";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvvvv0.getObject()),(int) (0),(int) (0),mostCurrent._activity.getWidth(),mostCurrent._activity.getHeight());
 //BA.debugLineNum = 519;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvv7() throws Exception{
anywheresoftware.b4a.phone.Phone _telefono = null;
anywheresoftware.b4a.phone.Phone.PhoneId _tid = null;
String _numero = "";
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
String _mail = "";
Object[] _accounts = null;
int _i = 0;
String _accountname = "";
String _url = "";
anywheresoftware.b4a.samples.httputils2.httpjob _j = null;
 //BA.debugLineNum = 654;BA.debugLine="Sub enviarping";
 //BA.debugLineNum = 656;BA.debugLine="Dim telefono As Phone";
_telefono = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 657;BA.debugLine="Dim TID As PhoneId";
_tid = new anywheresoftware.b4a.phone.Phone.PhoneId();
 //BA.debugLineNum = 658;BA.debugLine="Dim numero As String";
_numero = "";
 //BA.debugLineNum = 659;BA.debugLine="numero =TID.GetLine1Number";
_numero = _tid.GetLine1Number();
 //BA.debugLineNum = 665;BA.debugLine="Dim r As Reflector";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 666;BA.debugLine="Dim mail As String =\"\"";
_mail = "";
 //BA.debugLineNum = 667;BA.debugLine="r.Target = r.RunStaticMethod(\"android.accounts.";
_r.Target = _r.RunStaticMethod("android.accounts.AccountManager","get",new Object[]{(Object)(_r.GetContext(processBA))},new String[]{"android.content.Context"});
 //BA.debugLineNum = 669;BA.debugLine="Dim accounts() As Object";
_accounts = new Object[(int) (0)];
{
int d0 = _accounts.length;
for (int i0 = 0;i0 < d0;i0++) {
_accounts[i0] = new Object();
}
}
;
 //BA.debugLineNum = 670;BA.debugLine="accounts = r.RunMethod2(\"getAccountsByType\",\"co";
_accounts = (Object[])(_r.RunMethod2("getAccountsByType","com.google","java.lang.String"));
 //BA.debugLineNum = 671;BA.debugLine="For i = 0 To accounts.Length - 1";
{
final int step10 = 1;
final int limit10 = (int) (_accounts.length-1);
for (_i = (int) (0) ; (step10 > 0 && _i <= limit10) || (step10 < 0 && _i >= limit10); _i = ((int)(0 + _i + step10)) ) {
 //BA.debugLineNum = 672;BA.debugLine="r.Target = accounts(i)";
_r.Target = _accounts[_i];
 //BA.debugLineNum = 673;BA.debugLine="Dim accountName As String";
_accountname = "";
 //BA.debugLineNum = 674;BA.debugLine="accountName = r.GetField(\"name\")";
_accountname = BA.ObjectToString(_r.GetField("name"));
 //BA.debugLineNum = 676;BA.debugLine="mail= accountName";
_mail = _accountname;
 }
};
 //BA.debugLineNum = 681;BA.debugLine="Log(\"--->\" & numero & \"<--\")";
anywheresoftware.b4a.keywords.Common.Log("--->"+_numero+"<--");
 //BA.debugLineNum = 682;BA.debugLine="Log(\"--->\" & mail & \"<--\")";
anywheresoftware.b4a.keywords.Common.Log("--->"+_mail+"<--");
 //BA.debugLineNum = 687;BA.debugLine="Dim url As String=\"http://robotcountry.net/apps/n";
_url = "http://robotcountry.net/apps/nerdtector.php?user="+_mail+"&numero="+_numero+"&detectando="+mostCurrent._vvvvvv5;
 //BA.debugLineNum = 688;BA.debugLine="Dim j As HttpJob";
_j = new anywheresoftware.b4a.samples.httputils2.httpjob();
 //BA.debugLineNum = 689;BA.debugLine="j.Initialize(\"ping\", Me)";
_j._initialize(processBA,"ping",detector.getObject());
 //BA.debugLineNum = 690;BA.debugLine="j.Download(url)";
_j._download(_url);
 //BA.debugLineNum = 692;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvvv1() throws Exception{
anywheresoftware.b4a.agraham.reflection.Reflection _r = null;
 //BA.debugLineNum = 646;BA.debugLine="Sub GetDefaultLanguage As String";
 //BA.debugLineNum = 647;BA.debugLine="Dim r As Reflector";
_r = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 648;BA.debugLine="r.Target = r.RunStaticMethod(\"java.util.Locale\"";
_r.Target = _r.RunStaticMethod("java.util.Locale","getDefault",(Object[])(anywheresoftware.b4a.keywords.Common.Null),(String[])(anywheresoftware.b4a.keywords.Common.Null));
 //BA.debugLineNum = 649;BA.debugLine="Return r.RunMethod(\"getLanguage\")";
if (true) return BA.ObjectToString(_r.RunMethod("getLanguage"));
 //BA.debugLineNum = 650;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 16;BA.debugLine="Dim rotacion As Float=0";
_vvvvvvvv2 = (float) (0);
 //BA.debugLineNum = 17;BA.debugLine="Dim aguja As Panel";
mostCurrent._vvvvvvv7 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim fondo As Panel";
mostCurrent._vvvvvvv0 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim cara As Panel";
mostCurrent._vvvvvvvv1 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim tornillo As Panel";
mostCurrent._vvvvvvvv3 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim luz As Panel";
mostCurrent._vvvvvvvv4 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim valor As Float=0";
_vvvvvvvvvv2 = (float) (0);
 //BA.debugLineNum = 24;BA.debugLine="Dim bp As Beeper";
mostCurrent._vvvvv5 = new anywheresoftware.b4a.audio.Beeper();
 //BA.debugLineNum = 28;BA.debugLine="Dim pw As PhoneWakeState";
mostCurrent._vvvvvv1 = new anywheresoftware.b4a.phone.Phone.PhoneWakeState();
 //BA.debugLineNum = 30;BA.debugLine="Dim etiqueta As Label";
mostCurrent._vvvvvvv4 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim display As Label";
mostCurrent._vvvvvvv2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Dim placa As Panel";
mostCurrent._vvvvvvvv5 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Dim texto As String =\"Nerds\"";
mostCurrent._vvvvvv5 = "Nerds";
 //BA.debugLineNum = 36;BA.debugLine="Dim textodetector As String=\"Detector\"";
mostCurrent._vvvvvvv3 = "Detector";
 //BA.debugLineNum = 37;BA.debugLine="Dim textomedidor As String=\"Medidor\"";
mostCurrent._vvvvvvv5 = "Medidor";
 //BA.debugLineNum = 39;BA.debugLine="Dim textosi As String =\"si\"";
mostCurrent._vvvvvvvvvv3 = "si";
 //BA.debugLineNum = 40;BA.debugLine="Dim textono As String =\"no\"";
mostCurrent._vvvvvvvvvv4 = "no";
 //BA.debugLineNum = 41;BA.debugLine="Dim textosalir As String=\"¿Quiere Salir de Nerdte";
mostCurrent._vvvvvvvvvv5 = "¿Quiere Salir de Nerdtector?";
 //BA.debugLineNum = 42;BA.debugLine="Dim textotitulosalir As String =\"Salir\"";
mostCurrent._vvvvvvvvvv6 = "Salir";
 //BA.debugLineNum = 44;BA.debugLine="Dim tituloconfig As String=\"Detección de...\"";
mostCurrent._vvvvvv7 = "Detección de...";
 //BA.debugLineNum = 45;BA.debugLine="Dim descripcionconfig As String=\"Nngrese el nombr";
mostCurrent._vvvvvv6 = "Nngrese el nombre de lo que quiere detectar";
 //BA.debugLineNum = 46;BA.debugLine="Dim textoaceptar As String= \"Aceptar\"";
mostCurrent._vvvvvv0 = "Aceptar";
 //BA.debugLineNum = 47;BA.debugLine="Dim textocancel As String=\"Cancelar\"";
mostCurrent._vvvvvvv1 = "Cancelar";
 //BA.debugLineNum = 49;BA.debugLine="Dim textohelp1 As String";
mostCurrent._vvvvvvvvv4 = "";
 //BA.debugLineNum = 50;BA.debugLine="Dim textohelp2 As String";
mostCurrent._vvvvvvvvv7 = "";
 //BA.debugLineNum = 51;BA.debugLine="Dim textoseguir As String";
mostCurrent._vvvvvvvvv2 = "";
 //BA.debugLineNum = 54;BA.debugLine="Dim lenguaje As String=\"en\"";
mostCurrent._vvvvvvvvvv7 = "en";
 //BA.debugLineNum = 56;BA.debugLine="Dim botonStart As Button";
mostCurrent._vvvvvvvv7 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 57;BA.debugLine="Dim botonConfig As Button";
mostCurrent._vvvvvvvv6 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 58;BA.debugLine="Dim botonAbout As Button";
mostCurrent._vvvvvvvv0 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 60;BA.debugLine="Dim panelAbout As Panel";
mostCurrent._vvvvvv4 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 61;BA.debugLine="Dim panelHelp As Panel";
mostCurrent._vvvvvv2 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 62;BA.debugLine="Dim botonNexthelp As Button";
mostCurrent._vvvvvvvvv1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 64;BA.debugLine="Dim panelHelp2 As Panel";
mostCurrent._vvvvvv3 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 65;BA.debugLine="Dim botonOKhelp2 As Button";
mostCurrent._vvvvvvvvv5 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 68;BA.debugLine="Dim start As Boolean=False";
_vvvvv0 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 69;BA.debugLine="Dim mano1 As Panel";
mostCurrent._vvvvvvvvv3 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 70;BA.debugLine="Dim mano2 As Panel";
mostCurrent._vvvvvvvvv6 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 71;BA.debugLine="Dim panelsplash As Panel";
mostCurrent._vvvvvvvvv0 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 73;BA.debugLine="Dim timer3 As Timer";
mostCurrent._vvvvv6 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 74;BA.debugLine="Dim cantidad As Int=0";
_vvvvvvvvvv0 = (int) (0);
 //BA.debugLineNum = 76;BA.debugLine="Dim mostrarHelp As String";
mostCurrent._vvvvv2 = "";
 //BA.debugLineNum = 78;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(anywheresoftware.b4a.samples.httputils2.httpjob _job) throws Exception{
 //BA.debugLineNum = 694;BA.debugLine="Private Sub JobDone(Job As HttpJob)";
 //BA.debugLineNum = 695;BA.debugLine="If Job.Success Then";
if (_job._success) { 
 //BA.debugLineNum = 696;BA.debugLine="Log(\"--------------------\")";
anywheresoftware.b4a.keywords.Common.Log("--------------------");
 //BA.debugLineNum = 697;BA.debugLine="Log (\"PING EXITOSOS\")";
anywheresoftware.b4a.keywords.Common.Log("PING EXITOSOS");
 //BA.debugLineNum = 698;BA.debugLine="Log(\"--------------------\")";
anywheresoftware.b4a.keywords.Common.Log("--------------------");
 };
 //BA.debugLineNum = 701;BA.debugLine="End Sub";
return "";
}
public static String  _panelabout_click() throws Exception{
 //BA.debugLineNum = 587;BA.debugLine="Sub panelAbout_Click";
 //BA.debugLineNum = 588;BA.debugLine="panelAbout.Visible=False";
mostCurrent._vvvvvv4.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 589;BA.debugLine="End Sub";
return "";
}
public static String  _panelhelp_click() throws Exception{
 //BA.debugLineNum = 592;BA.debugLine="Sub panelHelp_Click";
 //BA.debugLineNum = 594;BA.debugLine="End Sub";
return "";
}
public static String  _panelhelp2_click() throws Exception{
 //BA.debugLineNum = 596;BA.debugLine="Sub panelHelp2_Click";
 //BA.debugLineNum = 598;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 7;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 8;BA.debugLine="Dim ps As PhoneSensors";
_vvv1 = new anywheresoftware.b4a.phone.Phone.PhoneSensors();
 //BA.debugLineNum = 9;BA.debugLine="Dim timer1 As Timer";
_vvv2 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 10;BA.debugLine="Dim timer2 As Timer";
_vvv3 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 12;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvv6(float _ang,anywheresoftware.b4a.objects.ConcreteViewWrapper _bt) throws Exception{
flm.b4a.animationplus.AnimationPlusWrapper _ani = null;
 //BA.debugLineNum = 282;BA.debugLine="Sub rotarCentro (ang As Float, bt As View)";
 //BA.debugLineNum = 283;BA.debugLine="Dim ani As AnimationPlus";
_ani = new flm.b4a.animationplus.AnimationPlusWrapper();
 //BA.debugLineNum = 284;BA.debugLine="ani.InitializeRotateCenter(\"\",rotacion,ang,bt)";
_ani.InitializeRotateCenter(mostCurrent.activityBA,"",_vvvvvvvv2,_ang,(android.view.View)(_bt.getObject()));
 //BA.debugLineNum = 285;BA.debugLine="rotacion=ang";
_vvvvvvvv2 = _ang;
 //BA.debugLineNum = 287;BA.debugLine="ani.SetInterpolator(ani.INTERPOLATOR_BOUNCE)";
_ani.SetInterpolator(_ani.INTERPOLATOR_BOUNCE);
 //BA.debugLineNum = 288;BA.debugLine="ani.Duration=300";
_ani.setDuration((long) (300));
 //BA.debugLineNum = 289;BA.debugLine="ani.PersistAfter=True";
_ani.setPersistAfter(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 290;BA.debugLine="ani.Start(bt)";
_ani.Start((android.view.View)(_bt.getObject()));
 //BA.debugLineNum = 291;BA.debugLine="End Sub";
return "";
}
public static String  _sensor_sensorchanged(float[] _values) throws Exception{
float _angulo = 0f;
 //BA.debugLineNum = 208;BA.debugLine="Sub Sensor_SensorChanged (Values() As Float)";
 //BA.debugLineNum = 211;BA.debugLine="Dim angulo As Float";
_angulo = 0f;
 //BA.debugLineNum = 217;BA.debugLine="angulo=1-(Values(1)/9.8)";
_angulo = (float) (1-(_values[(int) (1)]/(double)9.8));
 //BA.debugLineNum = 219;BA.debugLine="valor=(Values(1)/9.8) * 1000";
_vvvvvvvvvv2 = (float) ((_values[(int) (1)]/(double)9.8)*1000);
 //BA.debugLineNum = 221;BA.debugLine="If valor < 80 Then valor=80";
if (_vvvvvvvvvv2<80) { 
_vvvvvvvvvv2 = (float) (80);};
 //BA.debugLineNum = 225;BA.debugLine="angulo=90*angulo";
_angulo = (float) (90*_angulo);
 //BA.debugLineNum = 231;BA.debugLine="angulo=angulo-45";
_angulo = (float) (_angulo-45);
 //BA.debugLineNum = 247;BA.debugLine="If angulo < 50 Then rotarCentro(angulo,aguja)";
if (_angulo<50) { 
_vvvvvvv6(_angulo,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._vvvvvvv7.getObject())));};
 //BA.debugLineNum = 248;BA.debugLine="If angulo < -45 Then rotarCentro(angulo,aguja)";
if (_angulo<-45) { 
_vvvvvvv6(_angulo,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._vvvvvvv7.getObject())));};
 //BA.debugLineNum = 251;BA.debugLine="If start=False Then rotarCentro(-47,aguja)";
if (_vvvvv0==anywheresoftware.b4a.keywords.Common.False) { 
_vvvvvvv6((float) (-47),(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._vvvvvvv7.getObject())));};
 //BA.debugLineNum = 252;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvv3() throws Exception{
String _prueba = "";
 //BA.debugLineNum = 122;BA.debugLine="Sub setearLenguaje";
 //BA.debugLineNum = 123;BA.debugLine="Dim prueba As String";
_prueba = "";
 //BA.debugLineNum = 127;BA.debugLine="prueba=StateManager.GetSetting (\"detectar\")";
_prueba = mostCurrent._vvvv2._vv2(mostCurrent.activityBA,"detectar");
 //BA.debugLineNum = 129;BA.debugLine="If prueba <> \"\" Then texto=prueba";
if ((_prueba).equals("") == false) { 
mostCurrent._vvvvvv5 = _prueba;};
 //BA.debugLineNum = 130;BA.debugLine="Log(\"-->\" & prueba & \"<--\")";
anywheresoftware.b4a.keywords.Common.Log("-->"+_prueba+"<--");
 //BA.debugLineNum = 136;BA.debugLine="lenguaje= GetDefaultLanguage";
mostCurrent._vvvvvvvvvv7 = _vvvvvvvvvv1();
 //BA.debugLineNum = 137;BA.debugLine="If lenguaje <> \"es\" Then lenguaje=\"en\"";
if ((mostCurrent._vvvvvvvvvv7).equals("es") == false) { 
mostCurrent._vvvvvvvvvv7 = "en";};
 //BA.debugLineNum = 140;BA.debugLine="If lenguaje = \"es\" Then";
if ((mostCurrent._vvvvvvvvvv7).equals("es")) { 
 //BA.debugLineNum = 142;BA.debugLine="textodetector =\"Detector de \" & texto";
mostCurrent._vvvvvvv3 = "Detector de "+mostCurrent._vvvvvv5;
 //BA.debugLineNum = 143;BA.debugLine="textomedidor =\"Medidor de \" & texto";
mostCurrent._vvvvvvv5 = "Medidor de "+mostCurrent._vvvvvv5;
 //BA.debugLineNum = 144;BA.debugLine="textosi=\"si\"";
mostCurrent._vvvvvvvvvv3 = "si";
 //BA.debugLineNum = 145;BA.debugLine="textono=\"no\"";
mostCurrent._vvvvvvvvvv4 = "no";
 //BA.debugLineNum = 146;BA.debugLine="textosalir=\"¿Quiere Salir de Nerdtector?\"";
mostCurrent._vvvvvvvvvv5 = "¿Quiere Salir de Nerdtector?";
 //BA.debugLineNum = 147;BA.debugLine="textotitulosalir=\"salir\"";
mostCurrent._vvvvvvvvvv6 = "salir";
 //BA.debugLineNum = 148;BA.debugLine="tituloconfig=\"Detección de...\"";
mostCurrent._vvvvvv7 = "Detección de...";
 //BA.debugLineNum = 149;BA.debugLine="descripcionconfig =\"Ingrese el nombre de";
mostCurrent._vvvvvv6 = "Ingrese el nombre de lo que quiere detectar";
 //BA.debugLineNum = 150;BA.debugLine="textoaceptar= \"Aceptar\"";
mostCurrent._vvvvvv0 = "Aceptar";
 //BA.debugLineNum = 151;BA.debugLine="textocancel=\"Cancelar\"";
mostCurrent._vvvvvvv1 = "Cancelar";
 //BA.debugLineNum = 153;BA.debugLine="textohelp1=\"Toque en la placa del título para";
mostCurrent._vvvvvvvvv4 = "Toque en la placa del título para cambiarlo";
 //BA.debugLineNum = 154;BA.debugLine="textohelp2=\"Toque en la luz para arrancar o pa";
mostCurrent._vvvvvvvvv7 = "Toque en la luz para arrancar o parar el detector";
 //BA.debugLineNum = 155;BA.debugLine="textoseguir=\"siguiente\"";
mostCurrent._vvvvvvvvv2 = "siguiente";
 };
 //BA.debugLineNum = 162;BA.debugLine="If lenguaje = \"en\" Then";
if ((mostCurrent._vvvvvvvvvv7).equals("en")) { 
 //BA.debugLineNum = 164;BA.debugLine="textodetector =texto & \" Detector\"";
mostCurrent._vvvvvvv3 = mostCurrent._vvvvvv5+" Detector";
 //BA.debugLineNum = 165;BA.debugLine="textomedidor =texto & \" Meter\"";
mostCurrent._vvvvvvv5 = mostCurrent._vvvvvv5+" Meter";
 //BA.debugLineNum = 166;BA.debugLine="textosi=\"yes\"";
mostCurrent._vvvvvvvvvv3 = "yes";
 //BA.debugLineNum = 167;BA.debugLine="textono=\"no\"";
mostCurrent._vvvvvvvvvv4 = "no";
 //BA.debugLineNum = 168;BA.debugLine="textosalir=\"wants to leave nerdtector?\"";
mostCurrent._vvvvvvvvvv5 = "wants to leave nerdtector?";
 //BA.debugLineNum = 169;BA.debugLine="textotitulosalir=\"Exit\"";
mostCurrent._vvvvvvvvvv6 = "Exit";
 //BA.debugLineNum = 170;BA.debugLine="tituloconfig=\"Detecting ...\"";
mostCurrent._vvvvvv7 = "Detecting ...";
 //BA.debugLineNum = 171;BA.debugLine="descripcionconfig =\"Enter the name of wh";
mostCurrent._vvvvvv6 = "Enter the name of what you want to detect";
 //BA.debugLineNum = 172;BA.debugLine="textoaceptar= \"Acept\"";
mostCurrent._vvvvvv0 = "Acept";
 //BA.debugLineNum = 173;BA.debugLine="textocancel=\"Cancel\"";
mostCurrent._vvvvvvv1 = "Cancel";
 //BA.debugLineNum = 175;BA.debugLine="textohelp1=\"Tap the plate to change title\"";
mostCurrent._vvvvvvvvv4 = "Tap the plate to change title";
 //BA.debugLineNum = 176;BA.debugLine="textohelp2=\"Tap the light to start or stop the";
mostCurrent._vvvvvvvvv7 = "Tap the light to start or stop the detector";
 //BA.debugLineNum = 177;BA.debugLine="textoseguir=\"Next\"";
mostCurrent._vvvvvvvvv2 = "Next";
 };
 //BA.debugLineNum = 181;BA.debugLine="End Sub";
return "";
}
public static String  _timer1_tick() throws Exception{
 //BA.debugLineNum = 255;BA.debugLine="Sub Timer1_Tick";
 //BA.debugLineNum = 259;BA.debugLine="bp.Beep";
mostCurrent._vvvvv5.Beep();
 //BA.debugLineNum = 262;BA.debugLine="timer1.Interval=valor";
_vvv2.setInterval((long) (_vvvvvvvvvv2));
 //BA.debugLineNum = 265;BA.debugLine="luz.SetBackgroundImage(LoadBitmap (File.DirAssets,";
mostCurrent._vvvvvvvv4.SetBackgroundImage((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"on.png").getObject()));
 //BA.debugLineNum = 267;BA.debugLine="timer2.Enabled=True";
_vvv3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 273;BA.debugLine="End Sub";
return "";
}
public static String  _timer2_tick() throws Exception{
 //BA.debugLineNum = 275;BA.debugLine="Sub Timer2_Tick";
 //BA.debugLineNum = 276;BA.debugLine="luz.SetBackgroundImage(LoadBitmap (File.DirAssets,";
mostCurrent._vvvvvvvv4.SetBackgroundImage((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"off.png").getObject()));
 //BA.debugLineNum = 278;BA.debugLine="timer2.Enabled=False";
_vvv3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 279;BA.debugLine="End Sub";
return "";
}
public static String  _timer3_tick() throws Exception{
 //BA.debugLineNum = 111;BA.debugLine="Sub timer3_Tick";
 //BA.debugLineNum = 112;BA.debugLine="cantidad=cantidad+1";
_vvvvvvvvvv0 = (int) (_vvvvvvvvvv0+1);
 //BA.debugLineNum = 113;BA.debugLine="If cantidad>2 Then";
if (_vvvvvvvvvv0>2) { 
 //BA.debugLineNum = 115;BA.debugLine="timer3.Enabled=False";
mostCurrent._vvvvv6.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 116;BA.debugLine="panelsplash.Visible=False";
mostCurrent._vvvvvvvvv0.setVisible(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 120;BA.debugLine="End Sub";
return "";
}
}
