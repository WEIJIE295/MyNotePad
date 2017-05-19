# NotePad
功能如下：
* 显示时间戳
* 笔记查询功能
* 修改编辑时主题
* 笔记分类功能

## 一、显示时间戳
### 1、在listItem中添加一个TextView用来显示时间
```java
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_height="wrap_content"
    android:layout_width="match_parent"
    android:orientation="horizontal">
        <ImageView
        android:layout_width="70dp"
        android:layout_height="70dp"
        android:src="@drawable/notes"
        android:paddingLeft="10dp"
        />
    <RelativeLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content">
        <TextView
            android:id="@android:id/text1"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textAppearance="?android:attr/textAppearanceLarge"
            android:paddingLeft="5dip"
            android:paddingTop="7dp"
            android:text="123456"
        />
        <TextView
            android:id="@+id/dateText"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_below="@android:id/text1"
            android:paddingLeft="10dp"
            android:paddingTop="7dp"
            android:text="123456"/>
    </RelativeLayout>
</LinearLayout>
```
### 2、准备好显示时间的TextView,接下来把时间填进去。查看原来的代码，发现原来保存时间是以long类型保存的。我们把它转成date类型就ok了。
```java
 SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 String dateStr = dateformat.format(System.currentTimeMillis());
 values.put(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, dateStr);
 ```
 #### 上面代码是获取当前时间，转成我们熟悉的时间格式存入数据库，之后取出来显示就行了。

 ![Alt text](/app/src/main/res/drawable-hdpi/p1.png)
 -------
 ## 二、笔记查询功能
 ### 1、创建一个搜索对话框配置文件
 ```java
 <?xml version="1.0" encoding="utf-8"?>
<searchable xmlns:android="http://schemas.android.com/apk/res/android"
        android:label="@string/app_name"
        android:hint="@string/search_tip">
</searchable>
 ```
 ### 2、创建一个可用于搜索的Acitivity和执行搜索
 ```java
 public class SearchActivity extends ListActivity {

    private static final String[] PROJECTION = new String[] {
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, // 1
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, //2
            NotePad.Notes.COLUMN_NAME_GROUPID
    };

    private static final String selection = NotePad.Notes.COLUMN_NAME_TITLE+" like ? ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent.getData() == null) {
            intent.setData(NotePad.Notes.CONTENT_URI);
        }
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
           // Toast.makeText(this,query,Toast.LENGTH_SHORT).show();


           String [] selectionArgs = {query+"%"};

            Cursor cursor =  getContentResolver().query(
                    getIntent().getData(),            // Use the default content URI for the provider.
                    PROJECTION,                      // Return the note ID and title for each note.
                    selection,
                    selectionArgs,
                    NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
            );

            // The names of the cursor columns to display in the view, initialized to the title column
            String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE ,NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE} ;

            // The view IDs that will display the cursor columns, initialized to the TextView in
            // noteslist_item.xml
            int[] viewIDs = { android.R.id.text1 ,R.id.dateText };

            // Creates the backing adapter for the ListView.
            SimpleCursorAdapter adapter
                    = new SimpleCursorAdapter(
                    this,                             // The Context for the ListView
                    R.layout.noteslist_item,          // Points to the XML for a list item
                    cursor,                           // The cursor to get items from
                    dataColumns,
                    viewIDs
            );

            // Sets the ListView's adapter to be the cursor adapter that was just created.
            setListAdapter(adapter);

        }

    } 
  }
```
### 3、在AndroidManifest.xml中配置我们的activity
```java
<activity android:name=".SearchActivity">
            <intent-filter>
                <action android:name="android.intent.action.SEARCH" />
            </intent-filter>
            <meta-data android:name="android.app.searchable"
                android:resource="@xml/searchable"/>
</activity> 
<meta-data android:name="android.app.default_searchable"
        android:value="com.example.android.notepad.SearchActivity"/>
        
```
### 4、添加一个按钮来触发我们的搜索功能就完成了
#### 除了这种方法，可以自定义一个搜索对话框，根据搜索内容获取结果，然后刷新notelist就行了，这种方法也许大家比较熟悉。下面看上面这种方法的效果

 ![Alt text](/app/src/main/res/drawable-hdpi/p2.png)
 
 ![Alt text](/app/src/main/res/drawable-hdpi/p4.png)
  -------
  
  ## 三、修改编辑时主题
  ### 1、在editor_options_menu.xml中添加一个item，用来修改主题
  ### 2、给新加的item注册点击事件
  ```java
     @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle all of the possible menu actions.
        switch (item.getItemId()) {
        case R.id.menu_save:
            String text = mText.getText().toString();
            updateNote(text, null);
            finish();
            break;
        case R.id.menu_delete:
            deleteNote();
            finish();
            break;
        case R.id.menu_revert:
            cancelNote();
            break;
        case R.id.theme:
                theme();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
  ```
  ### 3、实现修改主题功能
  ```java
  private void theme(){
        final View editview = findViewById(R.id.note);
        AlertDialog.Builder builder = new AlertDialog.Builder(NoteEditor.this);
        final String[] colors = {"天蓝","草原","盛夏","浪漫"};
        builder.setTitle("更改主题");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String color = colors[i];
                switch (color){
                    case "天蓝":
                        editview.setBackgroundColor(Color.parseColor("#66ffff"));
                        break;
                    case "草原":
                        editview.setBackgroundColor(Color.parseColor("#009933"));
                        break;
                    case "盛夏":
                        editview.setBackgroundColor(Color.parseColor("#ffff66"));
                        break;
                    case "浪漫":
                        editview.setBackgroundColor(Color.parseColor("#ff0099"));
                        break;
                }

            }
        });
        builder.create();
        builder.show();
    }
  ```
  ### 截图
  
   ![Alt text](/app/src/main/res/drawable-hdpi/p6.png)
   ![Alt text](/app/src/main/res/drawable-hdpi/p7.png)
 ---------  

## 四、笔记分类
### 1、在数据库中新建一张表用来存储分类信息。我是在用NotePadProvider操作两张表，修改了原本的NotePadProvider代码。下面给出部分代码：
```java
  @Override
       public void onCreate(SQLiteDatabase db) {
           db.execSQL("CREATE TABLE " + NotePad.Notes.TABLE_NAME + " ("
                   + NotePad.Notes._ID + " INTEGER PRIMARY KEY,"
                   + NotePad.Notes.COLUMN_NAME_TITLE + " TEXT,"
                   + NotePad.Notes.COLUMN_NAME_NOTE + " TEXT,"
                   + NotePad.Notes.COLUMN_NAME_CREATE_DATE + " INTEGER,"
                   + NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE + " INTEGER,"
                   + NotePad.Notes.COLUMN_NAME_GROUPID + " INTEGER"
                   + ");");

           db.execSQL("CREATE TABLE " + NotePad.NotesGroup.TABLE_NAME +" ("
                   + NotePad.NotesGroup._ID + " INTEGER PRIMARY KEY,"
                   + NotePad.NotesGroup.COLUMN_NAME_GROUPTITLE + " TEXT"
                   + ");"
           );
       }
 
   @Override
   public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
           String sortOrder) {

       // Constructs a new query builder and sets its table name
       SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
       String orderBy = null;

       /**
        * Choose the projection and adjust the "where" clause based on URI pattern-matching.
        */
       switch (sUriMatcher.match(uri)) {
           // If the incoming URI is for notes, chooses the Notes projection
           case NOTES:
               qb.setTables(NotePad.Notes.TABLE_NAME);
               qb.setProjectionMap(sNotesProjectionMap);
               // If no sort order is specified, uses the default
               if (TextUtils.isEmpty(sortOrder)) {
                   orderBy = NotePad.Notes.DEFAULT_SORT_ORDER;
               } else {
                   // otherwise, uses the incoming sort order
                   orderBy = sortOrder;
               }
               break;

           /* If the incoming URI is for a single note identified by its ID, chooses the
            * note ID projection, and appends "_ID = <noteID>" to the where clause, so that
            * it selects that single note
            */
           case NOTE_ID:
               qb.setTables(NotePad.Notes.TABLE_NAME);
               qb.setProjectionMap(sNotesProjectionMap);
               qb.appendWhere(
                   NotePad.Notes._ID +    // the name of the ID column
                   "=" +
                   // the position of the note ID itself in the incoming URI
                   uri.getPathSegments().get(NotePad.Notes.NOTE_ID_PATH_POSITION));
               // If no sort order is specified, uses the default
               if (TextUtils.isEmpty(sortOrder)) {
                   orderBy = NotePad.Notes.DEFAULT_SORT_ORDER;
               } else {
                   // otherwise, uses the incoming sort order
                   orderBy = sortOrder;
               }
               break;
           //=====================================
           case NotesGroup:
               qb.setTables(NotePad.NotesGroup.TABLE_NAME);
               qb.setProjectionMap(sGroupProjectionMap);
               break;
           case NotesGroup_ID:
               qb.setTables(NotePad.NotesGroup.TABLE_NAME);
               qb.appendWhere(NotePad.NotesGroup._ID + "=" + uri.getPathSegments().get(NotePad.NotesGroup.NOTE_ID_PATH_POSITION));
               break;

           //=======================================
           case LIVE_FOLDER_NOTES:
               // If the incoming URI is from a live folder, chooses the live folder projection.
               qb.setProjectionMap(sLiveFolderProjectionMap);
               break;

           default:
               // If the URI doesn't match any of the known patterns, throw an exception.
               throw new IllegalArgumentException("Unknown URI " + uri);
       }


       // Opens the database object in "read" mode, since no writes need to be done.
       SQLiteDatabase db = mOpenHelper.getReadableDatabase();

       /*
        * Performs the query. If no problems occur trying to read the database, then a Cursor
        * object is returned; otherwise, the cursor variable contains null. If no records were
        * selected, then the Cursor object is empty, and Cursor.getCount() returns 0.
        */
       Cursor c = qb.query(
           db,            // The database to query
           projection,    // The columns to return from the query
           selection,     // The columns for the where clause
           selectionArgs, // The values for the where clause
           null,          // don't group the rows
           null,          // don't filter by row groups
           orderBy        // The sort order
       );

       // Tells the Cursor what URI to watch, so it knows when its source data changes
       c.setNotificationUri(getContext().getContentResolver(), uri);
       return c;
   }
```
### 2、在notepad契约类中加入分类表内容
```java
public static final class NotesGroup implements BaseColumns {
        private NotesGroup(){}
        public static final String TABLE_NAME = "notesGroup";

        private static final String SCHEME = "content://";


        private static final String PATH_NOTES_GROUP = "/notesGroup";
        private static final String PATH_NOTEGROUP_ID = "/notesGroup/";


        public static final int NOTE_ID_PATH_POSITION = 1;


        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_NOTES_GROUP);

        public static final Uri CONTENT_ID_URI_BASE
                = Uri.parse(SCHEME + AUTHORITY + PATH_NOTEGROUP_ID);

        public static final Uri CONTENT_ID_URI_PATTERN
                = Uri.parse(SCHEME + AUTHORITY + PATH_NOTEGROUP_ID + "/#");


        public static final String COLUMN_NAME_GROUPTITLE = "name";

}
```
### 3、在原本note表中加入一个noteGroup的外键把两张表关联起来。
### 4、根据分类显示对应的note信息，分类功能就完成了。
```java
private void createGrouplist() {
        //获取分类列表
        Cursor c = managedQuery(NotePad.NotesGroup.CONTENT_URI, GROUPSPRO, null, null, null);
        List<String> gourpList = new ArrayList<String>();
        // List<Integer> idList = new ArrayList<Integer>();
        while (c.moveToNext()) {
            gourpList.add(c.getString(c.getColumnIndex(NotePad.NotesGroup.COLUMN_NAME_GROUPTITLE)));
        }
        final TextView textView = (TextView) findViewById(R.id.textView1);
        if (!gourpList.isEmpty()) {
            textView.setText(gourpList.get(0));
            getIntent().putExtra("id", "1");
            int size = gourpList.size();
            final String[] groups = new String[size];
            //final int[] groupids = new int[size];
            for (int i = 0; i < size; i++) {
                groups[i] = gourpList.get(i);
                //  groupids[i] = idList.get(i);
            }

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NotesList.this);
                    builder.setTitle("切换分类");
                    builder.setItems(groups, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            textView.setText(groups[i]);
                            getIntent().putExtra("id", (i + 1) + "");
                            String[] selectionArgs = {(i + 1) + ""};
                            updateList(selectionArgs);
                        }
                    });
                    builder.create();
                    builder.show();
                }
            });
        } else {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(NotesList.this, "请先添加一个分类", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    private void updateList(String[] selectionArgs) {
        Cursor cursor = managedQuery(
                getIntent().getData(),            // Use the default content URI for the provider.
                PROJECTION,                       // Return the note ID and title for each note.
                selection,                             // No where clause, return all records.
                selectionArgs,                             // No where clause, therefore no where column values.
                NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
        );
        String[] dataColumns = {NotePad.Notes.COLUMN_NAME_TITLE, NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE};

        // The view IDs that will display the cursor columns, initialized to the TextView in
        // noteslist_item.xml
        int[] viewIDs = {android.R.id.text1, R.id.dateText};

        // Creates the backing adapter for the ListView.
        SimpleCursorAdapter adapter
                = new SimpleCursorAdapter(
                this,                             // The Context for the ListView
                R.layout.noteslist_item,          // Points to the XML for a list item
                cursor,                           // The cursor to get items from
                dataColumns,
                viewIDs
        );

        // Sets the ListView's adapter to be the cursor adapter that was just created.
        // setListAdapter(adapter);
        listView.setAdapter(adapter);
    }
```
### 截图

 ![Alt text](/app/src/main/res/drawable-hdpi/p9.png)
 ![Alt text](/app/src/main/res/drawable-hdpi/p10.png)
 ![Alt text](/app/src/main/res/drawable-hdpi/p11.png)
 ![Alt text](/app/src/main/res/drawable-hdpi/p12.png)
 
---------
 




 
