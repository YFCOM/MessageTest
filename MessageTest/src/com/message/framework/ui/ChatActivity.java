package com.message.framework.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.example.messagetest.R;







public class ChatActivity extends Activity{

	int[] faceId={R.drawable.f_static_000,R.drawable.f_static_001,R.drawable.f_static_002,R.drawable.f_static_003
			,R.drawable.f_static_004,R.drawable.f_static_005,R.drawable.f_static_006,R.drawable.f_static_009,R.drawable.f_static_010,R.drawable.f_static_011
			,R.drawable.f_static_012,R.drawable.f_static_013,R.drawable.f_static_014,R.drawable.f_static_015,R.drawable.f_static_017,R.drawable.f_static_018};
	String[] faceName={"\\呲牙","\\淘气","\\流汗","\\偷笑","\\再见","\\敲打","\\擦汗","\\流泪","\\掉泪","\\小声","\\炫酷","\\发狂"
			 ,"\\委屈","\\便便","\\菜刀","\\微笑","\\色色","\\害羞"};
	
	HashMap<String,Integer> faceMap=null;
	ArrayList<HashMap<String,Object>> chatList=null;
	String[] from={"image","text"};
	int[] to={R.id.chatlist_image_me,R.id.chatlist_text_me,R.id.chatlist_image_other,R.id.chatlist_text_other};
	int[] layout={R.layout.chat_listitem_me,R.layout.chat_listitem_other};
	String userQQ=null;
	/**
	 * 这里两个布局文件使用了同一个id，测试一下是否管用
	 * TT事实证明这回产生id的匹配异常！所以还是要分开。。
	 * 
	 * userQQ用于接收Intent传递的qq号，进而用来调用数据库中的相关的联系人信息，这里先不讲
	 * 先暂时使用一个头像
	 */
	
	public final static int OTHER=1;
	public final static int ME=0;
	
	ArrayList<ImageView> pointList=null;
	ArrayList<ArrayList<HashMap<String,Object>>> listGrid=null;
	protected ListView chatListView=null;
	protected Button chatSendButton=null;
	protected EditText editText=null;
	protected ViewFlipper viewFlipper=null;
	protected ImageButton chatBottomLook=null;
	protected RelativeLayout faceLayout=null;
	protected LinearLayout pagePoint=null,fillGapLinear=null;
   
	private boolean expanded=false;
	
	
	
	protected MyChatAdapter adapter=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);
	
		faceMap=new HashMap<String,Integer>();	
		chatList=new ArrayList<HashMap<String,Object>>();
		listGrid=new ArrayList<ArrayList<HashMap<String,Object>>>();
        pointList=new ArrayList<ImageView>();
		
		addTextToList("不管你是谁", ME);
		addTextToList("群发的我不回\n  ^_^", OTHER);
		addTextToList("哈哈哈哈", ME);
		addTextToList("新年快乐！", OTHER);
		
		chatSendButton=(Button)findViewById(R.id.chat_bottom_sendbutton);
		editText=(EditText)findViewById(R.id.chat_bottom_edittext);
		chatListView=(ListView)findViewById(R.id.chat_list);
		viewFlipper=(ViewFlipper)findViewById(R.id.faceFlipper);
		chatBottomLook=(ImageButton)findViewById(R.id.chat_bottom_look);
		faceLayout=(RelativeLayout)findViewById(R.id.faceLayout);
		pagePoint=(LinearLayout)findViewById(R.id.pagePoint);
		fillGapLinear=(LinearLayout)findViewById(R.id.fill_the_gap);
		
		chatBottomLook.setOnClickListener(new OnClickListener(){
  
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(expanded){
					setFaceLayoutExpandState(false);
					expanded=false;
					
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  

					/**height不设为0是因为，希望可以使再次打开时viewFlipper已经初始化为第一页 避免
					*再次打开ViewFlipper时画面在动的结果,
					*为了避免因为1dip的高度产生一个白缝，所以这里在ViewFlipper所在的RelativeLayout
					*最上面添加了一个1dip高的黑色色块
					*/
					
					
				}
				else{

					setFaceLayoutExpandState(true);  
					expanded=true;
				    setPointEffect(0);

				}
			}
			
		});
		
		/**EditText从未获得焦点到首次获得焦点时不会调用OnClickListener方法，所以应该改成OnTouchListener
		 * 从而保证点EditText第一下就能够把表情界面关闭
		editText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ViewGroup.LayoutParams params=viewFlipper.getLayoutParams();
				params.height=0;
				viewFlipper.setLayoutParams(params);
				expanded=false;
				System.out.println("WHYWHWYWHYW is Clicked");
			}
			
		});
		**/
		editText.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(expanded){
					
					setFaceLayoutExpandState(false);
					expanded=false;
				}
				return false;
			}
		});
		adapter=new MyChatAdapter(this,chatList,layout,from,to);			
		chatSendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String myWord=null;
				
				/**
				 * 这是一个发送消息的监听器，注意如果文本框中没有内容，那么getText()的返回值可能为
				 * null，这时调用toString()会有异常！所以这里必须在后面加上一个""隐式转换成String实例
				 * ，并且不能发送空消息。
				 */
				
				myWord=(editText.getText()+"").toString();
				if(myWord.length()==0)
					return;
				
				addTextToList(myWord, ME);
				

				// 获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
				
				
				EMConversation conversation = EMChatManager.getInstance()
						.getConversation("123");
				// 创建一条文本消息
				EMMessage message = EMMessage
						.createSendMessage(EMMessage.Type.TXT);
				// 如果是群聊，设置chattype,默认是单聊
				// message.setChatType(ChatType.GroupChat);
				// 设置消息body
				
				String content = editText.getText().toString();
				if(!TextUtils.isEmpty(content)){
					TextMessageBody txtBody = new TextMessageBody(content);
					message.addBody(txtBody);
				}
				editText.setText("");
				
				
				// 设置接收人
				message.setReceipt("123");
				// 把消息加入到此会话对象中
				conversation.addMessage(message);
				// 发送消息
				EMChatManager.getInstance().sendMessage(message,
						new EMCallBack() {

							@Override
							public void onError(int arg0, String arg1) {
								// TODO Auto-generated method stub
								Log.d("yfcLog", "发送失败" + arg1);
							}

							@Override
							public void onProgress(int arg0, String arg1) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								Log.d("yfcLog", "发送成功");
							}
						});
			
				/**
				 * 更新数据列表，并且通过setSelection方法使ListView始终滚动在最底端
				 */
				adapter.notifyDataSetChanged();
				chatListView.setSelection(chatList.size()-1);
				
			} 
		});
		
		chatListView.setAdapter(adapter);
		
		chatListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				setFaceLayoutExpandState(false);
				((InputMethodManager)ChatActivity.this.getSystemService(INPUT_METHOD_SERVICE)).
				hideSoftInputFromWindow(ChatActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			    expanded=false;
			}
		});
		
		/**
		 * 为表情Map添加数据
		 */
		for(int i=0; i<faceId.length; i++){
			faceMap.put(faceName[i], faceId[i]);
		}
		
		
		addFaceData();
		addGridView();
		
		
	}
	
	private void addFaceData(){
		ArrayList<HashMap<String,Object>> list=null;
		for(int i=0; i<faceId.length; i++){
			if(i%14==0){
				list=new ArrayList<HashMap<String,Object>>();
				listGrid.add(list);
			}  
			HashMap<String,Object> map=new HashMap<String,Object>();
			map.put("image", faceId[i]);
			map.put("faceName", faceName[i]);
			
			/**
			 * 这里把表情对应的名字也添加进数据对象中，便于在点击时获得表情对应的名称
			 */
			listGrid.get(i/14).add(map);		
		}
		System.out.println("listGrid size is "+listGrid.size());
	}
	
	
	private void addGridView(){
		for(int i=0; i< listGrid.size();i++){
			View view=LayoutInflater.from(this).inflate(R.layout.view_item, null);
			GridView gv=(GridView)view.findViewById(R.id.myGridView);
			gv.setNumColumns(5);
			gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
			MyGridAdapter adapter=new MyGridAdapter(this, listGrid.get(i), R.layout.chat_grid_item, new String[]{"image"}, new int[]{R.id.gridImage});
			gv.setAdapter(adapter);
			gv.setOnTouchListener(new MyTouchListener(viewFlipper));
			viewFlipper.addView(view);
		//	ImageView image=new ImageView(this);
		//	ImageView image=(ImageView)LayoutInflater.from(this).inflate(R.layout.image_point_layout, null);
            /**
             * 这里不喜欢用Java代码设置Image的边框大小等，所以单独配置了一个Imageview的布局文件
             */
			View pointView=LayoutInflater.from(this).inflate(R.layout.point_image_layout, null);
			ImageView image=(ImageView)pointView.findViewById(R.id.pointImageView);
			image.setBackgroundResource(R.drawable.qian_point);
			pagePoint.addView(pointView);   
			/**
			 * 这里验证了LinearLayout属于ViewGroup类型，可以采用addView 动态添加view
			 */
			
			pointList.add(image);
			/**
			 * 将image放入pointList，便于修改点的颜色
			 */
		}
	
	}
	
	/**
	 * 打开或者关闭软键盘，之前若打开，调用该方法后关闭；之前若关闭，调用该方法后打开
	 */
	
	private void setSoftInputState(){
		((InputMethodManager)ChatActivity.this.getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	private void setFaceLayoutExpandState(boolean isexpand){
		if(isexpand==false){

			viewFlipper.setDisplayedChild(0);	
			ViewGroup.LayoutParams params=faceLayout.getLayoutParams();
			params.height=1;
			faceLayout.setLayoutParams(params);	
			/**height不设为0是因为，希望可以使再次打开时viewFlipper已经初始化为第一页 避免
			*再次打开ViewFlipper时画面在动的结果,
			*为了避免因为1dip的高度产生一个白缝，所以这里在ViewFlipper所在的RelativeLayout中ViewFlipper
			*上层添加了一个1dip高的黑色色块
			*
			*viewFlipper必须在屏幕中有像素才能执行setDisplayedChild()操作
			*/
			chatBottomLook.setBackgroundResource(R.drawable.chat_bottom_look);
			
			
		}
		else{
			/**
			 * 让软键盘消失
			 */
			((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
			(ChatActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

			
			
			ViewGroup.LayoutParams params=faceLayout.getLayoutParams();
			params.height=150;
			faceLayout.setLayoutParams(params);    
		    chatBottomLook.setBackgroundResource(R.drawable.chat_bottom_keyboard);

		}
	}
	
	/**
	 * 设置游标（小点）的显示效果
	 * @param darkPointNum
	 */
	private void setPointEffect(int darkPointNum){
		for(int i=0; i<pointList.size(); i++){
			pointList.get(i).setBackgroundResource(R.drawable.qian_point);
		}
		pointList.get(darkPointNum).setBackgroundResource(R.drawable.shen_point);
	}
	
	/**
	 * GridViewAdapter
	 * @param textView
	 * @param text
	 */
	
	class MyGridAdapter extends BaseAdapter{

		Context context;
		ArrayList<HashMap<String,Object>> list;
		int layout;
		String[] from;
		int[] to;
		
		
		public MyGridAdapter(Context context,
				ArrayList<HashMap<String, Object>> list, int layout,
				String[] from, int[] to) {
			super();
			this.context = context;
			this.list = list;
			this.layout = layout;
			this.from = from;
			this.to = to;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		class ViewHolder{
			ImageView image=null;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder=null;
			if(convertView==null){
				convertView=LayoutInflater.from(context).inflate(layout, null);
				holder=new ViewHolder();
				holder.image=(ImageView)convertView.findViewById(to[0]);
				convertView.setTag(holder);
			}
			else{
				holder=(ViewHolder)convertView.getTag();
			}
			holder.image.setImageResource((Integer)list.get(position).get(from[0]));
			class MyGridImageClickListener implements OnClickListener{

				int position;
				
				public MyGridImageClickListener(int position) {
					super();
					this.position = position;
				}


				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					editText.append((String)list.get(position).get("faceName"));
				}
				
			}
			//这里创建了一个方法内部类
			holder.image.setOnClickListener(new MyGridImageClickListener(position));
			
			
			
			return convertView;
		}
		
	}
	
	
	private boolean moveable=true;
	private float startX=0;
	
	/**
	 * 用到的方法 viewFlipper.getDisplayedChild()  获得当前显示的ChildView的索引
	 * @author Administrator
	 *
	 */
	class MyTouchListener implements OnTouchListener{

		ViewFlipper viewFlipper=null;
		
		
		public MyTouchListener(ViewFlipper viewFlipper) {
			super();
			this.viewFlipper = viewFlipper;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:startX=event.getX(); moveable=true; break;
			case MotionEvent.ACTION_MOVE:
				if(moveable){
					if(event.getX()-startX>60){
						moveable=false;
						int childIndex=viewFlipper.getDisplayedChild();
						/**
						 * 这里的这个if检测是防止表情列表循环滑动
						 */
						if(childIndex>0){
						    viewFlipper.setInAnimation(AnimationUtils.loadAnimation(ChatActivity.this, R.anim.left_in));
						    viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(ChatActivity.this, R.anim.right_out));						
							viewFlipper.showPrevious();
							setPointEffect(childIndex-1);
						}
					}
					else if(event.getX()-startX<-60){
						moveable=false;
						int childIndex=viewFlipper.getDisplayedChild();
						/**
						 * 这里的这个if检测是防止表情列表循环滑动
						 */
						if(childIndex<listGrid.size()-1){
							viewFlipper.setInAnimation(AnimationUtils.loadAnimation(ChatActivity.this, R.anim.right_in));
							viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(ChatActivity.this, R.anim.left_out));
							viewFlipper.showNext();
							setPointEffect(childIndex+1);
						}
					}
				}
			    break;
			case MotionEvent.ACTION_UP:moveable=true;break;
			default:break;
			}
			
			return false;
		}
		
	}
	
	
	
	private void setFaceText(TextView textView,String text){
		SpannableString spanStr=parseString(text);
		textView.setText(spanStr);
	}
	
	private void setFace(SpannableStringBuilder spb, String faceName){
		Integer faceId=faceMap.get(faceName);
		if(faceId!=null){
			Bitmap bitmap=BitmapFactory.decodeResource(getResources(), faceId);
			bitmap=Bitmap.createScaledBitmap(bitmap, 30, 30, true);
			ImageSpan imageSpan=new ImageSpan(this,bitmap);
			SpannableString spanStr=new SpannableString(faceName);
			spanStr.setSpan(imageSpan, 0, faceName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			spb.append(spanStr);	
		}
		else{
			spb.append(faceName);
		}
		
	}
	
	private SpannableString parseString(String inputStr){
		SpannableStringBuilder spb=new SpannableStringBuilder();
		Pattern mPattern= Pattern.compile("\\\\..");
		Matcher mMatcher=mPattern.matcher(inputStr);
		String tempStr=inputStr;
		
		while(mMatcher.find()){
			int start=mMatcher.start();
			int end=mMatcher.end();
			spb.append(tempStr.substring(0,start));
			String faceName=mMatcher.group();
			setFace(spb, faceName);
			tempStr=tempStr.substring(end, tempStr.length());
			/**
			 * 更新查找的字符串
			 */
			mMatcher.reset(tempStr);
		}
		spb.append(tempStr);
		return new SpannableString(spb);
	}
	
	
	
	protected void addTextToList(String text, int who){
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("person",who );
		map.put("image", who==ME?R.drawable.contact_0:R.drawable.contact_1);
		map.put("text", text);
		chatList.add(map);
	}
	
	private class MyChatAdapter extends BaseAdapter{

		Context context=null;
		ArrayList<HashMap<String,Object>> chatList=null;
		int[] layout;
		String[] from;
		int[] to;
		  
		
		
		public MyChatAdapter(Context context,
				ArrayList<HashMap<String, Object>> chatList, int[] layout,
				String[] from, int[] to) {
			super();
			this.context = context;
			this.chatList = chatList;
			this.layout = layout;
			this.from = from;
			this.to = to;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return chatList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		class ViewHolder{
			public ImageView imageView=null;
			public TextView textView=null;
		
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder=null;
			int who=(Integer)chatList.get(position).get("person");
 
				convertView= LayoutInflater.from(context).inflate(
						layout[who==ME?0:1], null);
				holder=new ViewHolder();
				holder.imageView=(ImageView)convertView.findViewById(to[who*2+0]);
				holder.textView=(TextView)convertView.findViewById(to[who*2+1]);
			
			
			System.out.println(holder);
			System.out.println(holder.imageView);
			holder.imageView.setBackgroundResource((Integer)chatList.get(position).get(from[0]));
			setFaceText(holder.textView, chatList.get(position).get(from[1]).toString());
			return convertView;
		}
		
	}
	
	

}
