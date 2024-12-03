package com.phincode.honnywellintermecpr3;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
 import android.os.AsyncTask;
 import android.annotation.SuppressLint;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import com.honeywell.mobility.print.LinePrinter;
import com.honeywell.mobility.print.LinePrinterException;
import com.honeywell.mobility.print.PrintProgressEvent;
import com.honeywell.mobility.print.PrintProgressListener;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.res.AssetManager;

/** Honnywellintermecpr3Plugin */

public class Honnywellintermecpr3Plugin  implements FlutterPlugin, MethodCallHandler{
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private String jsonCmdAttribStr = null;
  Context c;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "honnywellintermecpr3");
    channel.setMethodCallHandler(this);
    c=flutterPluginBinding.getApplicationContext();
  }
 
  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    // if (call.method.equals("printImg")) {

    //   String deviceName = call.argument("deviceName");
    //   String deviceBleutoothMacAdress = call.argument("deviceBleutoothMacAdress");
    //   String imageb64 = call.argument("imageb64");
    //   Intent intent = new Intent(this.c,PrintActivity.class);
    //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //   intent.putExtra("deviceName",deviceName);
    //   intent.putExtra("deviceBleutoothMacAdress",deviceBleutoothMacAdress);
    //   intent.putExtra("imageb64",imageb64);
    //   startActivity(this.c,intent,null);



    // } else if(call.method.equals("printGeneralWithActivity")){ 

    //   String deviceName = call.argument("deviceName");
    //   String deviceBleutoothMacAdress = call.argument("deviceBleutoothMacAdress");
    //   ArrayList<String> commande = call.argument("cmd");
    //   Log.d ("cmd",commande.toString());
    //   Intent intent = new Intent(this.c,PrintActivity.class);
    //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //   intent.putExtra("deviceName",deviceName);
    //   intent.putExtra("deviceBleutoothMacAdress",deviceBleutoothMacAdress);
    //   intent.putExtra("cmd",commande);
    //   startActivity(this.c,intent,null);
      
    //   } else 
	  if(call.method.equals("printGeneral")){

	try{
       String deviceName = call.argument("deviceName");
      String deviceBleutoothMacAdress = call.argument("deviceBleutoothMacAdress");
      ArrayList<String> commande = call.argument("cmd");
      Log.d ("cmd",commande.toString());
      readAssetFiles();


					PrintTask task = new PrintTask();

			PrinterTaskParams params = new PrinterTaskParams(commande, deviceName,c,deviceBleutoothMacAdress,result);
				
				Log.d ("=================>EXECUTING TASK","==================");
						task.execute(params);
						
			Log.d ("=================>EXECUTING DONE","==================");


				// Intent intent = new Intent(this.c,PrintActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// intent.putExtra("deviceName",deviceName);
				// intent.putExtra("deviceBleutoothMacAdress",deviceBleutoothMacAdress);
				// intent.putExtra("cmd",commande);
				// startActivity(this.c,intent,null);
			// result.success("done");
			}
   catch( Exception e){
           System.out.println(e);
                // Handle communications error here.
                e.printStackTrace();
result.error("Error","e",null);
   }
    }
    else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);

  }



	private void readAssetFiles()
	{
		InputStream input = null;
		ByteArrayOutputStream output = null;
		AssetManager assetManager = c.getAssets();
		String[] files = { "printer_profiles.JSON", "honeywell_logo.bmp" };
		int fileIndex = 0;
		int initialBufferSize;

		try
		{
			for (String filename : files)
			{
				input = assetManager.open(filename);
				initialBufferSize = (fileIndex == 0) ? 8000 : 2500;
				output = new ByteArrayOutputStream(initialBufferSize);

				byte[] buf = new byte[1024];
				int len;
				while ((len = input.read(buf)) > 0)
				{
					output.write(buf, 0, len);
				}
				input.close();
				input = null;

				output.flush();
				output.close();
				switch (fileIndex)
				{
				case 0:
					jsonCmdAttribStr = output.toString();
					break;
				/*case 1:
					base64LogoPng = Base64.encodeToString(output.toByteArray(), Base64.DEFAULT);
					break;*/
				}

				fileIndex++;
				output = null;
			}
		}
		catch (Exception ex)
		{
			// textMsg.append("Error reading asset file: " + files[fileIndex]);
		}
		finally
		{
			try
			{
				if (input != null)
				{
					input.close();
					input = null;
				}

				if (output != null)
				{
					output.close();
					output = null;
				}
			}
			catch (IOException e){

      }
		}
	}
class PrinterTaskParams {
    ArrayList<String> linesToPrint;
    String printerName;
    String printerAddresss;
    Context cont;
	Result result;

    PrinterTaskParams(ArrayList<String> linesToPrint, String printerHeader, Context cont  , String printerAddresss,Result result) {
        this.linesToPrint = linesToPrint;
        this.printerName = printerName;
        this.printerAddresss = printerAddresss;
        this.cont = cont;
		this.result = result;
    }
}
	public class PrintTask extends AsyncTask<PrinterTaskParams, Integer, String> {
		private static final String PROGRESS_CANCEL_MSG = "Printing cancelled\n";
		private static final String PROGRESS_COMPLETE_MSG = "Printing completed\n";
		private static final String PROGRESS_ENDDOC_MSG = "End of document\n";
		private static final String PROGRESS_FINISHED_MSG = "Printer connection closed\n";
		private static final String PROGRESS_NONE_MSG = "Unknown progress message\n";
		private static final String PROGRESS_STARTDOC_MSG = "Start printing document\n";


		/**
		 * Runs on the UI thread before doInBackground(Params...).
		 */
		@Override
		protected void onPreExecute()
		{
      		Log.d("=============> PRE-EXECUTE", "==================");
			// Clears the Progress and Status text box.
			// textMsg.setText("");

			// Disables the Print button.
			// buttonPrint.setEnabled(false);
			// Disables the Sign button.
			// buttonSign.setEnabled(false);

			// Shows a progress icon on the title bar to indicate
			// it is working on something.
			// setProgressBarIndeterminateVisibility(true);
		}

		/**
		 * This method runs on a background thread. The specified parameters
		 * are the parameters passed to the execute method by the caller of
		 * this task. This method can call publishProgress to publish updates
		 * on the UI thread.
		 */
		@SuppressLint("WrongThread")
		@Override
		protected String doInBackground(PrinterTaskParams... args)
		{
			LinePrinter lp = null;
			String sResult = null;

      		PrinterTaskParams param = args[0];
			String sPrinterID = param.printerName;
			String sPrinterAddr = param.printerAddresss;
      		ArrayList<String> commande = param.linesToPrint;
			Result flutterResult = param.result;

      		Context lContext = param.cont;
			String sDocNumber = "1234567890";
			String sPrinterURI = null;

			// if(connectionTypes.getSelectedItem().toString().equals(
			// 		getResources().getString(R.string.bluetooth_connection)))
			// {
				// The printer address should be a Bluetooth MAC address.
				if (sPrinterAddr.contains(":") == false && sPrinterAddr.length() == 12)
				{
					// If the MAC address only contains hex digits without the
					// ":" delimiter, then add ":" to the MAC address string.
					char[] cAddr = new char[17];

					for (int i=0, j=0; i < 12; i += 2)
					{
						sPrinterAddr.getChars(i, i+2, cAddr, j);
						j += 2;
						if (j < 17)
						{
							cAddr[j++] = ':';
						}
					}

					sPrinterAddr = new String(cAddr);
				}

				sPrinterURI = "bt://" + sPrinterAddr;
			// }
			// else if(connectionTypes.getSelectedItem().toString().equals(
			// 		getResources().getString(R.string.serial_connection)))
			// {
			// 	// The printer address should be a serial port name.
			// 	sPrinterURI = "serial://" + sPrinterAddr;
			// }
			Log.d("------------ LP CONTEXT","-----------");
			LinePrinter.ExtraSettings exSettings = new LinePrinter.ExtraSettings();

			exSettings.setContext(lContext);
			Log.d("------------ LP DONE CONTEXT","-----------");
			PrintProgressListener progressListener =
				new PrintProgressListener()
				{
					@Override
					public void receivedStatus(PrintProgressEvent aEvent)
					{
						// Publishes updates on the UI thread.
						// publishProgress(aEvent.getMessageType());
					}
				};

			try
			{
			Log.d("------------ LP PRINT","-----------");
				lp = new LinePrinter(
						jsonCmdAttribStr,
						"PR3",
						sPrinterURI,
						exSettings);
  			Log.d("------------ LP PRINT= INIT DONE","-----------");
				// Registers to listen for the print progress events.
				lp.addPrintProgressListener(progressListener);

				//A retry sequence in case the bluetooth socket is temporarily not ready
				int numtries = 0;
				int maxretry = 2;
				while(numtries < maxretry)
				{
					try{
						lp.connect();  // Connects to the printer
						break;
					}
					catch(LinePrinterException ex){
						numtries++;
						Thread.sleep(1000);
					}
				}
				if (numtries == maxretry) lp.connect();//Final retry

				// Check the state of the printer and abort printing if there are
				// any critical errors detected.
				// int[] results = lp.getStatus();
				// if (results != null)
				// {
				// 	for (int err = 0; err < results.length; err++)
				// 	{
				// 		if (results[err] == 223)
				// 		{
				// 			// Paper out.
				// 			// throw new BadPrinterStateException("Paper out");
				// 		}
				// 		else if (results[err] == 227)
				// 		{
				// 			// Lid open.
				// 			// throw new BadPrinterStateException("Printer lid open");
				// 		}
				// 	}
				// }
					int ofset=0;
					int widh=0;
					int heigh=0;

                if(commande!=null){
					for (String cmd : commande) {
						if(cmd.startsWith("write")){
							String v=cmd.substring(cmd.indexOf(";")+1,cmd.length());
							lp.write(v);
						}
						if(cmd.startsWith("newLine")){
							String v=cmd.substring(cmd.indexOf(";")+1,cmd.length());
							int val=Integer.parseInt(v);
							lp.newLine(val);

						}
						if(cmd.startsWith("setBold")){
							String v=cmd.substring(cmd.indexOf(";")+1,cmd.length());
							 if(v.contains("true")){
								 lp.setBold(true);
							 }else{
								 lp.setBold(false);
							 }
						}
						if(cmd.startsWith("setDoubleWide")){
							String v=cmd.substring(cmd.indexOf(";")+1,cmd.length());
							if(v.contains("true")){
								lp.setDoubleWide(true);
							}else{
								lp.setDoubleWide(false);
							}

						}
						if(cmd.startsWith("setDoubleHigh")){
							String v=cmd.substring(cmd.indexOf(";")+1,cmd.length());
							if(v.contains("true")){
								lp.setDoubleHigh(true);
							}else{
								lp.setDoubleHigh(false);
							}
						}
						if(cmd.startsWith("offset")){
							String v=cmd.substring(cmd.indexOf(";")+1,cmd.length());
							ofset=Integer.parseInt(v);

						}
						if(cmd.startsWith("width")){
							String v=cmd.substring(cmd.indexOf(";")+1,cmd.length());
							widh=Integer.parseInt(v);

						}
						if(cmd.startsWith("heigh")){
							String v=cmd.substring(cmd.indexOf(";")+1,cmd.length());
							heigh=Integer.parseInt(v);

						}
						if(cmd.startsWith("image")){
							String v=cmd.substring(cmd.indexOf(";")+1,cmd.length());

							lp.writeGraphicBase64(v.toString(),
									LinePrinter.GraphicRotationDegrees.DEGREE_0,
									ofset,  // Offset in printhead dots from the left of the page
									widh, // Desired graphic width on paper in printhead dots
									heigh);
						}
					}

				}
				 
				sResult = "Number of bytes sent to printer: " + lp.getBytesWritten();
				// flutterResult.success("PRINT DONE");
			}
			// catch (Exception ex)
			// {
			// 	// Stop listening for printer events.
			// 	lp.removePrintProgressListener(progressListener);
			// 	sResult = "Printer error detected: " + ex.getMessage() + ". Please correct the error and try again.";
			// }
			catch (LinePrinterException ex)
			{
				sResult = "LinePrinterException: " + ex.getMessage();
				flutterResult.error("Intermec Error: ",sResult,null);
			}
			catch (Exception ex)
			{
				if (ex.getMessage() != null)
					sResult = "Unexpected exception: " + ex.getMessage();
				else
					sResult = "Unexpected exception.";
				flutterResult.error("Intermec Error: ",sResult,null);
			}
			finally
			{
				if (lp != null)
				{
					try
					{
						lp.disconnect();  // Disconnects from the printer
						lp.close();  
						flutterResult.success("SUCCESS");// Releases resources
					}
					catch (Exception ex) {
						flutterResult.error("Disconnect Error - Intermec: ",sResult,null);
					}
				}
			}
				Log.d("===========> RESULT",sResult);
			// The result string will be passed to the onPostExecute method
			// for display in the the Progress and Status text box.
			return sResult;
		}

		/**
		 * Runs on the UI thread after publishProgress is invoked. The
		 * specified values are the values passed to publishProgress.
		 */
		@Override
		protected void onProgressUpdate(Integer... values)
		{
			// Access the values array.
			int progress = values[0];

			switch (progress)
			{
			case PrintProgressEvent.MessageTypes.CANCEL:
				// textMsg.append(PROGRESS_CANCEL_MSG);
				break;
			case PrintProgressEvent.MessageTypes.COMPLETE:
				// textMsg.append(PROGRESS_COMPLETE_MSG);
				break;
			case PrintProgressEvent.MessageTypes.ENDDOC:
				// textMsg.append(PROGRESS_ENDDOC_MSG);
				break;
			case PrintProgressEvent.MessageTypes.FINISHED:
				// textMsg.append(PROGRESS_FINISHED_MSG);
				break;
			case PrintProgressEvent.MessageTypes.STARTDOC:
				// textMsg.append(PROGRESS_STARTDOC_MSG);
				break;
			default:
				// textMsg.append(PROGRESS_NONE_MSG);
				break;
			}
		}

		/**
		 * Runs on the UI thread after doInBackground method. The specified
		 * result parameter is the value returned by doInBackground.
		 */
		@Override
		protected void onPostExecute(String result)
		{
			// Displays the result (number of bytes sent to the printer or
			// exception message) in the Progress and Status text box.
			if (result != null)
			{
				// textMsg.append(result);
			}

			// // Dismisses the progress icon on the title bar.
			// setProgressBarIndeterminateVisibility(false);

			// // Enables the Print button.
			// buttonPrint.setEnabled(true);
			// // Enables the Sign button.
			// buttonSign.setEnabled(true);
		}
	} 
}
