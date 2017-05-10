Type=Activity
Version=6.5
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: True
	#IncludeTitle: False
#End Region

'Activity module
Sub Process_Globals
    Dim ps As PhoneSensors
	Dim timer1 As Timer
	Dim timer2 As Timer
	
End Sub


Sub Globals
	Dim rotacion As Float=0
	Dim aguja As Panel
	Dim fondo As Panel
	Dim cara As Panel
	Dim tornillo As Panel
	Dim luz As Panel
	
	Dim valor As Float=0
	Dim bp As Beeper
	
	
	
	Dim pw As PhoneWakeState
	
	Dim etiqueta As Label
	Dim display As Label
	Dim placa As Panel
	
	
	Dim texto As String ="Nerds"
	Dim textodetector As String="Detector"
	Dim textomedidor As String="Medidor"
	
	Dim textosi As String ="si"
	Dim textono As String ="no"
	Dim textosalir As String="¿Quiere Salir de Nerdtector?"
	Dim textotitulosalir As String ="Salir"
	
	Dim tituloconfig As String="Detección de..."
	Dim descripcionconfig As String="Nngrese el nombre de lo que quiere detectar"
	Dim textoaceptar As String= "Aceptar"
	Dim textocancel As String="Cancelar"
	
	Dim textohelp1 As String
	Dim textohelp2 As String
	Dim textoseguir As String
	
	
	Dim lenguaje As String="en"
	
	Dim botonStart As Button
	Dim botonConfig As Button
	Dim botonAbout As Button
	
	Dim panelAbout As Panel
	Dim panelHelp As Panel
	Dim botonNexthelp As Button
	
	Dim panelHelp2 As Panel
	Dim botonOKhelp2 As Button
	
	
    Dim start As Boolean=False
	Dim mano1 As Panel
	Dim mano2 As Panel
	Dim panelsplash As Panel
	
	Dim timer3 As Timer
	Dim cantidad As Int=0

    Dim mostrarHelp As String
	
End Sub


Sub Activity_Create(FirstTime As Boolean)
	
	mostrarHelp=StateManager.GetSetting ("mostrarhelp") 
		
	If mostrarHelp <> "no" Then	
		StateManager.SetSetting("mostrarhelp", "no" )
 		StateManager.SaveSettings
	End If	
	
	
	
    If FirstTime Then
        ps.Initialize(ps.TYPE_ACCELEROMETER)

		setearLenguaje
		dibujar
				
		bp.Initialize2(100,2500,bp.VOLUME_MUSIC)
		timer1.Initialize("Timer1", 1000)
   		
		timer2.Initialize("Timer2", 100)
	    
        timer3.Initialize("timer3", 500)
		timer3.Enabled=True
	    enviarping
		
    End If
End Sub


Sub timer3_Tick
   cantidad=cantidad+1
   If cantidad>2 Then
      
	  timer3.Enabled=False
	  panelsplash.Visible=False	
	
   End If

End Sub

Sub setearLenguaje
	    Dim prueba As String
		
        'se verifica si se grabo
		'el seteo de que se detecta	    
		prueba=StateManager.GetSetting ("detectar") 
	    
	   	If prueba <> "" Then texto=prueba
		Log("-->" & prueba & "<--")
	
	
	   'se mira el lenguaje del telefono
		'si no es español se pone en ingles
		'solo dos lenguajes español o ingles		
		lenguaje= GetDefaultLanguage
		If lenguaje <> "es" Then lenguaje="en"
		
		
		If lenguaje = "es" Then
			 
	         textodetector ="Detector de " & texto
	         textomedidor ="Medidor de " & texto
			 textosi="si"
			 textono="no"
			 textosalir="¿Quiere Salir de Nerdtector?"
			 textotitulosalir="salir"
			 tituloconfig="Detección de..."
	         descripcionconfig ="Ingrese el nombre de lo que quiere detectar"
			 textoaceptar= "Aceptar"
			 textocancel="Cancelar"
			 
			 textohelp1="Toque en la placa del título para cambiarlo"
			 textohelp2="Toque en la luz para arrancar o parar el detector"
	         textoseguir="siguiente"
	
			 
		End If
		
		
		
		If lenguaje = "en" Then
			 
	         textodetector =texto & " Detector"
	         textomedidor =texto & " Meter"
			 textosi="yes"
			 textono="no"
			 textosalir="wants to leave nerdtector?"
			 textotitulosalir="Exit"
			 tituloconfig="Detecting ..."
	         descripcionconfig ="Enter the name of what you want to detect"
			 textoaceptar= "Acept"
			 textocancel="Cancel"
			 
			 textohelp1="Tap the plate to change title"
			 textohelp2="Tap the light to start or stop the detector"
	         textoseguir="Next"
			 
		End If
		
End Sub


Sub Activity_Resume
    
   
	
	
	If start= True Then timer1.Enabled = True 
		  
	pw.KeepAlive(True)
End Sub

Sub Activity_Pause (UserClosed As Boolean)
	
ExitApplication


   
''   ps.StopListening
''   timer1.Enabled = False
''   pw.ReleaseKeepAlive
End Sub




Sub Sensor_SensorChanged (Values() As Float)
    
	   
		Dim angulo As Float
		
		
		
        'cuando el telefono esta parado angulo es = 0
		'cuando el telefono esta acostado angulo es = 1		
		angulo=1-(Values(1)/9.8)
		
		valor=(Values(1)/9.8) * 1000
		
		If valor < 80 Then valor=80
		
		'si se multiplica abgulo por 90
		'la excursion total va a ser de 90 grados	
		angulo=90*angulo
		
		'la aguja cuando esta vertical esta a cero grados
		'cuando el telefon esta parado angulo es cero
		'si se le resta 45 la aguja en reposo esta a menos 45grados
		'de la vertical
		angulo=angulo-45
		
		
		
'		If valor < 500 Then 
'			display.TextColor=Colors.ARGB(230,255,0,0)
'		Else
'		  display.TextColor=Colors.ARGB(220,0,0,0)
'		End If
		
        
'	Log(angulo)
'	Log( valor)
'	Log("#### " & timer1.Interval)
	
	
	  If angulo < 50 Then rotarCentro(angulo,aguja)
	  If angulo < -45 Then rotarCentro(angulo,aguja)
	  
	 
	  If start=False Then rotarCentro(-47,aguja)	
End Sub


Sub Timer1_Tick
	
		
 
 bp.Beep

	
timer1.Interval=valor
	

luz.SetBackgroundImage(LoadBitmap (File.DirAssets,"on.png"))

timer2.Enabled=True


	

	
End Sub

Sub Timer2_Tick
luz.SetBackgroundImage(LoadBitmap (File.DirAssets,"off.png"))

timer2.Enabled=False
End Sub


Sub rotarCentro (ang As Float, bt As View)
	Dim ani As AnimationPlus
	ani.InitializeRotateCenter("",rotacion,ang,bt)
	rotacion=ang	
	
	ani.SetInterpolator(ani.INTERPOLATOR_BOUNCE)
	ani.Duration=300
	ani.PersistAfter=True
	ani.Start(bt)
End Sub


Sub dibujar 
	 
	Dim ancho As Float= Activity.width
	Dim pivotx As Float
	Dim largoAguja As Float =ancho *0.35
	Dim largoTotalaguja As Float =largoAguja * 2
	
	'la escala adapta los difrentes tamaños y "numeros magicos" de
	'tamaños a las diferentes pantallas, asumiendo como referencia
  	'el tamaño de mi alcatel que es de 4.5
  	Dim escala As Float = GetDeviceLayoutValues.ApproximateScreenSize / 4.5   
	
	
	fondo.Initialize("")
	fondo.SetBackgroundImage(LoadBitmap (File.DirAssets,"fondo.png"))
	Activity.AddView(fondo,0, 0,ancho,Activity.Height)
	
	
   'la cara del instrumentoes cuadrada. 572 pixeles x 572 pixeles	
	cara.Initialize("")
	cara.SetBackgroundImage(LoadBitmap (File.DirAssets,"cara.png"))
	Activity.AddView(cara,0, 20dip,ancho,ancho)
	
	
	
   'la etiqueta que esta adentro del medidor	
	Dim anchoetiqueta As Float=ancho*0.30
	Dim altotiqueta As Float=ancho*0.1
	etiqueta.Initialize("")
'	etiqueta.Color= Colors.Cyan
	Activity.AddView(etiqueta, (Activity.Width/2)-(anchoetiqueta/2), ancho*0.47, anchoetiqueta, altotiqueta)
	etiqueta.Gravity=Gravity.CENTER
	etiqueta.TextColor=Colors.ARGB(225,0,0,0)
	
	'setea elinterlineado de la label
	Dim Obj1 As Reflector
    Obj1.Target = etiqueta
    Obj1.RunMethod3("setLineSpacing", 1, "java.lang.float", 0.7, "java.lang.float")
	
	'#############################
    'EL TEXTO DEL MEDIDOR
	'#############################	
	etiqueta.Text=textomedidor
	etiqueta.TextSize=14
	
	
	'pivotx es donde se insetra la  aguja
	pivotx= 22dip+((ancho *0.7)-largoAguja)
	
	
	
	'se dibuja e inicializa la aguja
	 aguja.Initialize("")
	 aguja.SetBackgroundImage(LoadBitmap (File.DirAssets,"aguja.png"))
     Activity.AddView(aguja,Activity.Width/2, pivotx,10dip, largoTotalaguja)
'	 aguja.top=Activity.Height-aguja.Height
	 aguja.Left=1dip+(Activity.Width-aguja.Width)/2
		
	 rotacion=-47
	 rotarCentro(rotacion,aguja)	
	 
	 
	 'el tornillo	
	tornillo.Initialize("")
	Dim anchotornillo As Float= ancho * 0.05944056
	Dim posxTornillo As Float=(ancho/2)- (anchotornillo/2)
	Dim posyTornillo As Float=20dip+ancho*0.6870629
	
	tornillo.SetBackgroundImage(LoadBitmap (File.DirAssets,"tornillo.png"))
	Activity.AddView(tornillo,posxTornillo, posyTornillo,anchotornillo,anchotornillo)
	 
   'la lucecita indicadora	
	Dim anchoLuz As Float=80dip 
	luz.Initialize("") 
	
	luz.SetBackgroundImage(LoadBitmap (File.DirAssets,"off.png"))
	Activity.AddView(luz,(Activity.Width/2)-(anchoLuz/2), ancho+20dip,anchoLuz,anchoLuz)
	
	
	
	placa.Initialize("")
	placa.SetBackgroundImage(LoadBitmap (File.DirAssets,"placa.png"))
	
	
	'el display
	Dim font As Typeface
	Dim anchodisplay As Float=ancho*0.75
	Dim altodisplay As Float=anchodisplay*0.42
	Dim posyDisplay As Float=(luz.Top+luz.Height)+10dip
	
	font=Typeface.LoadFromAssets("helvetica.ttf")
	
	display.Initialize("")
'	display.Color= Colors.Cyan

    Activity.AddView(placa, (Activity.Width/2)-(anchodisplay/2), posyDisplay, anchodisplay, altodisplay)

    anchodisplay=anchodisplay*0.9
	Activity.AddView(display, (Activity.Width/2)-(anchodisplay/2), posyDisplay, anchodisplay, altodisplay)
	display.Gravity=Gravity.CENTER
	display.TextColor=Colors.ARGB(255,136,106,80)
	display.Typeface=font
	
	
	'setea elinterlineado de la label
	Dim Obj1 As Reflector
    Obj1.Target = display
    Obj1.RunMethod3("setLineSpacing", 1, "java.lang.float", 0.9, "java.lang.float")
	
	'#############################
    'EL TEXTO de la placa
	'#############################	
	display.Text=textodetector
	display.TextSize=30
	
	
	'#############################
    'los botones
	'#############################
	botonConfig.Initialize("botonConfig")	
	botonStart.Initialize("botonStart")
	botonAbout.Initialize("botonAbout")
	
	'el boton de configuracion
	Activity.AddView(botonConfig,0, posyDisplay,ancho,altodisplay)
	
	
	'el boton de activar
	Activity.AddView(botonStart,0, ancho+20dip,ancho,anchoLuz)
	
	'el boton de about
	Activity.AddView(botonAbout,0, 0,ancho,ancho)
	
	botonConfig.Color=Colors.Transparent
	botonStart.Color=Colors.Transparent
	botonAbout.Color=Colors.Transparent
	 
	 
	'#############################
    'el panel about
	'############################# 
	 
	 panelAbout.Initialize("panelAbout")
	 panelAbout.SetBackgroundImage(LoadBitmap (File.DirAssets,"about.png"))
	 panelAbout.Visible=False
	 Activity.AddView(panelAbout,0, 0,Activity.width,Activity.Height)
	 
	 
	'#############################
    'el panel help
	'############################# 
	 
	 
	 
If mostrarHelp <> "no"	Then	 
	
	    '###### el panel help 1 #############################
		 panelHelp.Initialize("panelHelp")
		 panelHelp.Color=Colors.ARGB(180,0,0,0)
		 
		 botonNexthelp.Initialize("botonNexthelp")
		 botonNexthelp.Color=Colors.ARGB(255,57,186,234)
		 botonNexthelp.Text=textoseguir
		 botonNexthelp.TextColor=Colors.White
		 
		 Dim anchoBoton As Float=150dip
		 Dim altoboton As Float=40dip
		 Dim anchomano=Activity.Width/3
		 panelHelp.AddView(botonNexthelp,ancho-anchoBoton-20dip,Activity.Height-altoboton-20dip,anchoBoton,altoboton)
		 
		 Activity.AddView(panelHelp,0, 0,Activity.width,Activity.Height)
		 
		 mano1.Initialize("")
		 mano1.SetBackgroundImage(LoadBitmap (File.DirAssets,"mano2.png"))
		 panelHelp.AddView(mano1,0,0,anchomano,anchomano)
		 
		 mano1.Left=(Activity.Width/2)-anchomano
		 mano1.Top=display.Top-(anchomano/2)
		 
		 Dim labelhelp1 As Label
		 labelhelp1.Initialize("")
		 labelhelp1.TextColor=Colors.ARGB(255,57,186,234)
		 panelHelp.AddView(labelhelp1, 0,0,Activity.Width,70dip)
		 labelhelp1.Text=textohelp1
		 labelhelp1.TextSize=28
		 labelhelp1.Top=mano1.Top-labelhelp1.Height
		 labelhelp1.Gravity=Bit.Or( Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL)
		 
		 '###### el panel help 2 #############################
		 panelHelp2.Initialize("panelHelp2")
		 panelHelp2.Color=Colors.ARGB(180,0,0,0)
		 
		 botonOKhelp2.Initialize("botonOKhelp2")
		 botonOKhelp2.Color=Colors.ARGB(255,57,186,234)
		 botonOKhelp2.Text="OK"
		 botonOKhelp2.TextColor=Colors.White
		 
		
		 panelHelp2.AddView(botonOKhelp2,ancho-anchoBoton-20dip,Activity.Height-altoboton-20dip,anchoBoton,altoboton)
		 
		 
		 Activity.AddView(panelHelp2,Activity.width, 0,Activity.width,Activity.Height)
		 

		 mano2.Initialize("")
		 mano2.SetBackgroundImage(LoadBitmap (File.DirAssets,"mano2.png"))
		 panelHelp2.AddView(mano2,0,0,anchomano,anchomano)
		 
		 mano2.Left=(Activity.Width/2)-anchomano
		 mano2.Top=luz.Top-(anchomano/2)
		 
		 Dim labelhelp2 As Label
		 labelhelp2.Initialize("")
		 labelhelp2.TextColor=Colors.ARGB(255,57,186,234)
		 panelHelp2.AddView(labelhelp2, 0,0,Activity.Width,70dip)
		 labelhelp2.Text=textohelp2
		 labelhelp2.TextSize=28
		 labelhelp2.Top=mano2.Top-labelhelp2.Height
		 labelhelp2.Gravity=Bit.Or( Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL)
End If	 
	 
	 panelsplash.Initialize("")
	 panelsplash.SetBackgroundImage(LoadBitmap (File.DirAssets,"splash.png"))
	 Activity.AddView(panelsplash,0, 0,Activity.width,Activity.Height)
	 
End Sub

'#############################
'los eventos de los botones
'#############################

Sub botonConfig_Click
	Dim p As Phone
	Dim dialogo As InputDialog
	Dim accion As Int
	
	If start=True Then 
		timer1.Enabled = False 
		ps.StopListening
	End If
	
	dialogo.Input=texto
	accion=dialogo.Show(descripcionconfig,tituloconfig, textoaceptar, textocancel,"", Null)
	
	
	If accion = DialogResponse.POSITIVE Then
		texto=dialogo.Input
		StateManager.SetSetting("detectar", texto )
 		StateManager.SaveSettings
		setearLenguaje
		display.Text=textodetector
		etiqueta.Text=textomedidor
    End If
   
   p.HideKeyboard(Activity)
   
   If start=True Then 
		timer1.Enabled=True
		 ps.StartListening("Sensor") 
	End If
   
End Sub





Sub botonStart_Click

    If start= False Then
		 timer1.Enabled = True
		 ps.StartListening("Sensor") 
		 start=True  
	   Else
	    timer1.Enabled = False 
		ps.StopListening
		start=False  
		
	    rotarCentro(-47,aguja)	
	End If
   
 

'   Dim result As Int
'   result = Msgbox2(textosalir, textotitulosalir, textosi, "", textono,  Null)
'   If result = DialogResponse.Positive Then ExitApplication
   
End Sub

Sub botonAbout_LongClick
  	 panelAbout.Visible=True
End Sub

Sub panelAbout_Click
  	 panelAbout.Visible=False
End Sub


Sub panelHelp_Click
  	
End Sub

Sub panelHelp2_Click
  	
End Sub

Sub botonNexthelp_Click
	
	
	Dim anim As AnimationPlus
	anim.InitializeTranslate("animation",0,0,-Activity.Width,0)
	anim.Duration=200
	anim.PersistAfter=False
	anim.Start(panelHelp)
	
End Sub

Sub animation_AnimationEnd
	

	panelHelp.Left=-Activity.Width
	Dim anim As AnimationPlus
	anim.InitializeTranslate("animation1",0,0,-Activity.Width,0)
	anim.Duration=100
	anim.PersistAfter=False
	anim.Start(panelHelp2)
	
End Sub

Sub animation1_AnimationEnd
	
 panelHelp2.left=0
End Sub

Sub botonOKhelp2_Click
	
	Dim anim As AnimationPlus
	anim.InitializeTranslate("animation2",0,0,-Activity.Width,0)
	anim.Duration=100
	anim.PersistAfter=True
	anim.Start(panelHelp2)
End Sub


Sub animation2_AnimationEnd
	panelHelp2.Enabled=False
	panelHelp2.left=-Activity.Width
	
'	panelHelp2.RemoveView
	
End Sub

Sub GetDefaultLanguage As String
   Dim r As Reflector
   r.Target = r.RunStaticMethod("java.util.Locale", "getDefault", Null, Null)
   Return r.RunMethod("getLanguage")
End Sub



Sub enviarping
	
	Dim telefono As Phone
	Dim TID As PhoneId
	Dim numero As String
	numero =TID.GetLine1Number
	 
	
   '************************************************ 
   ' obtener mail
   '************************************************ 	
   Dim r As Reflector
   Dim mail As String =""
   r.Target = r.RunStaticMethod("android.accounts.AccountManager", "get", _
   Array As Object(r.GetContext), Array As String("android.content.Context"))
   Dim accounts() As Object
   accounts = r.RunMethod2("getAccountsByType","com.google", "java.lang.String")
   For i = 0 To accounts.Length - 1
      r.Target = accounts(i)
      Dim accountName As String
      accountName = r.GetField("name")
	  
	  mail= accountName
   Next
	
	
		
	Log("--->" & numero & "<--")
	Log("--->" & mail & "<--")
	
	'************************************************ 
    ' se envia un ping a robotcountry
	'************************************************ 
	Dim url As String="http://robotcountry.net/apps/nerdtector.php?user="& mail & "&numero=" & numero & "&detectando=" & texto
	Dim j As HttpJob
    j.Initialize("ping", Me) 
    j.Download(url)
	
End Sub

Private Sub JobDone(Job As HttpJob)
	If Job.Success Then
			Log("--------------------")
		    Log ("PING EXITOSOS")
		    Log("--------------------")
		
	End If
End Sub