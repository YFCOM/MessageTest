package com.example.messagetest;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;

public class MainActivity extends Activity {

	private EditText editname, editpasswd;
	private Button button;
	private Button loginButton;
	private Button sendButton;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				Bundle bundle = msg.getData();
				String res = bundle.getString("content");
				String message = bundle.getString("writeMessage");
				Log.d("yfcLog", "content tran" + res);

				try {
					JSONObject jsonObject = new JSONObject(res);
					if (jsonObject.has("trans_result")) {
						String middle = jsonObject.getString("trans_result");
						String middle2 = middle.substring(1,
								middle.length() - 1);
						// text.setText(middle2);
						JSONObject jsonObjectR = new JSONObject(middle2);
						if (jsonObjectR.has("dst")) {
//							mConversationArrayAdapter.add("Me:  "
//									+ jsonObjectR.getString("dst") + "\n"
//									+ message);
							Log.d("yfcLog", "dst is"+jsonObjectR.getString("dst"));
						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		NewMessageBroadcastReceiver msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);

		editname = (EditText) findViewById(R.id.name);
		editpasswd = (EditText) findViewById(R.id.passwd);
		button = (Button) findViewById(R.id.button);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String name = editname.getText().toString();
				final String passwd = editpasswd.getText().toString();
				// TODO Auto-generated method stub

				new Thread() {
					public void run() {
						try {
							// 调用sdk注册方法
							EMChatManager.getInstance().createAccountOnServer(
									name, passwd);
						} catch (final EaseMobException e) {
							// 注册失败
							int errorCode = e.getErrorCode();
							if (errorCode == EMError.NONETWORK_ERROR) {
								// Toast.makeText(MainActivity.this,
								// "网络异常，请检查网络！", Toast.LENGTH_SHORT)
								// .show();
								Log.d("yfcLog", "网络异常，请检查网络");
							} else if (errorCode == EMError.USER_ALREADY_EXISTS) {
								// Toast.makeText(MainActivity.this,
								// "用户已存在！", Toast.LENGTH_SHORT).show();
								Log.d("yfcLog", "用户已经存在");
							} else if (errorCode == EMError.UNAUTHORIZED) {
								// Toast.makeText(MainActivity.this,
								// "注册失败，无权限！", Toast.LENGTH_SHORT).show();
								Log.d("yfcLog", "没有权限");
							} else {
								// Toast.makeText(MainActivity.this,
								// "注册失败: " + e.getMessage(),
								// Toast.LENGTH_SHORT).show();
								Log.d("yfcLog", "注册失败" + e.getMessage());
							}
						}
					}
				}.start();

			}
		});
		loginButton = (Button) findViewById(R.id.loginbutton);
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String name = editname.getText().toString();
				final String passwd = editpasswd.getText().toString();
				EMChatManager.getInstance().login(name, passwd,
						new EMCallBack() {// 回调
							@Override
							public void onSuccess() {
								runOnUiThread(new Runnable() {
									public void run() {
										EMGroupManager.getInstance()
												.loadAllGroups();
										EMChatManager.getInstance()
												.loadAllConversations();
										Log.d("yfcLog", "登陆聊天服务器成功！");
									}
								});
							}

							@Override
							public void onProgress(int progress, String status) {

							}

							@Override
							public void onError(int code, String message) {
								Log.d("yfcLog", "登陆聊天服务器失败！");
							}
						});
			}
		});
		sendButton = (Button) findViewById(R.id.sendbutton);
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// //获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
				// EMConversation conversation =
				// EMChatManager.getInstance().getConversation("123");
				// //创建一条文本消息
				// EMMessage message =
				// EMMessage.createSendMessage(EMMessage.Type.TXT);
				// //如果是群聊，设置chattype,默认是单聊
				// // message.setChatType(ChatType.GroupChat);
				// //设置消息body
				// TextMessageBody txtBody = new TextMessageBody("从123发来的消息");
				// message.addBody(txtBody);
				// //设置接收人
				// message.setReceipt("123");
				// //把消息加入到此会话对象中
				// conversation.addMessage(message);
				// //发送消息
				// EMChatManager.getInstance().sendMessage(message, new
				// EMCallBack(){
				//
				// @Override
				// public void onError(int arg0, String arg1) {
				// // TODO Auto-generated method stub
				// Log.d("yfcLog", "发送失败"+arg1);
				// }
				//
				// @Override
				// public void onProgress(int arg0, String arg1) {
				// // TODO Auto-generated method stub
				//
				// }
				//
				// @Override
				// public void onSuccess() {
				// // TODO Auto-generated method stub
				// Log.d("yfcLog", "发送成功");
				// }});
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ChatActivity.class);
				startActivity(intent);
			}
		});
	}

	public class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 注销广播
			abortBroadcast();

			// 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
			String msgId = intent.getStringExtra("msgid");
			// 发送方
			String username = intent.getStringExtra("from");

			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);

			final String messagebody = message.getBody().toString();

			Log.d("yfcLog", "消息ID是：" + msgId + "用户姓名是：" + username + "消息内容是"
					+ message);
			Toast.makeText(MainActivity.this,
					"消息ID是：" + msgId + "用户姓名是：" + username + "消息内容是" + message,
					Toast.LENGTH_SHORT).show();

			EMConversation conversation = EMChatManager.getInstance()
					.getConversation(username);
			// 如果是群聊消息，获取到group id
			if (message.getChatType() == ChatType.GroupChat) {
				username = message.getTo();
			}
			if (!username.equals(username)) {
				// 消息不是发给当前会话，return
				return;
			}

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					HttpGet getMethod = new HttpGet(
							"http://openapi.baidu.com/public/2.0/bmt/translate?client_id=6nUHD8NnpgbtVYKsXvS5lsUa&q="
									+ "你好" + "&from=zh&to=en");

					HttpClient httpClient = new DefaultHttpClient();

					try {
						HttpResponse response = httpClient.execute(getMethod); // 发起GET请求

						String res = EntityUtils.toString(response.getEntity(),
								"utf-8");
						Log.i("yfcLog", "resCode = "
								+ response.getStatusLine().getStatusCode()); // 获取响应码
						Log.i("yfcLog", "result = " + res);// 获取服务器响应内容

						Message message = new Message();
						Bundle bundle = new Bundle();
						bundle.putString("content", res);
						bundle.putString("writeMessage", "");
						message.setData(bundle);
						message.what = 1;
						handler.sendMessage(message);
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}

	}

}
