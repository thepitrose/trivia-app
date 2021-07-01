package com.example.myapplication2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.net.Uri;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class CreatePersonalCategory extends AppCompatActivity {
    private static final int PICKFILE_RESULT_CODE = 5149; // Some id number to find out where the result came from

    private String userID;
    private String categoryName;
    private ArrayList<Question> questions;
    private ArrayAdapter adapter;

    private TextView tvCategoryName;
    private ListView questionsView;

    private EditText newCategoryName;

    private Button bAddPrivateCategory;
    private Button bSavePrivateCategory;
    private Document fileInput;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent returnIntent) {
        super.onActivityResult(requestCode, resultCode, returnIntent);
        Uri uri = returnIntent.getData();

        String filePath = null;
        try {
            filePath = FileUtils.getPath(this.getApplicationContext(), uri);
            if(ParseFile(filePath))
                UpdateView();
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }


    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private boolean ParseFile(String fileName) {
        try {
            File file = new File(fileName);
            fileInput = parseFile(file);

            categoryName = fileInput.getElementsByTagName("Category").item(0).getAttributes().item(0).getTextContent();
            NodeList questionsNode = fileInput.getElementsByTagName("Question");

            questions = new ArrayList<>();

            for (int i = 0; i < questionsNode.getLength(); i++) {
                Node q = questionsNode.item(i);

                //System.out.println("\nCurrent Element :" + q.getNodeName());
                if (q.getNodeType() == Node.ELEMENT_NODE) {

                    Element qElement = (Element)q;
                    String questionText = qElement.getElementsByTagName("QuestionText").item(0).getTextContent();
                    String rightAnswer = qElement.getElementsByTagName("RightAnswer").item(0).getTextContent();
                    List<String> wrongs = new ArrayList<>();
                    NodeList wrongsNode = qElement.getElementsByTagName("WrongAnswer");

                    for (int j = 0; j < wrongsNode.getLength(); j++) {
                        Node w = wrongsNode.item(j);
                        if (w.getNodeType() == Node.ELEMENT_NODE) {
                            Element wElement = (Element)w;
                            wrongs.add(wElement.getTextContent());
                        }
                    }

                    questions.add(new Question(questionText, rightAnswer, wrongs));

                }
            }
        }
        catch (Exception e){
            Toast.makeText(CreatePersonalCategory.this , "Error reading category file: Message: " + e.getMessage() , Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private Document parseFile(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        // TODO: take this return statment out of comment and delete al the code after it till the end of the function
        return documentBuilder.parse(new FileInputStream(file));

        /*
        Document doc = documentBuilder.newDocument();

        Element Category = doc.createElement("Category");
        doc.appendChild(Category);
        Category.setAttribute("Name", "MeirBirthday");
        Element Question1 = doc.createElement("Question");
        Element Question2 = doc.createElement("Question");
        Element Question3 = doc.createElement("Question");
        Category.appendChild(Question1);
        Category.appendChild(Question2);
        Category.appendChild(Question3);

        Element QuestionText = doc.createElement("QuestionText");
        QuestionText.appendChild(doc.createTextNode("1 plus 1 is"));
        Element RightAnswer = doc.createElement("RightAnswer");
        RightAnswer.appendChild(doc.createTextNode("2"));
        Element WrongAnswer1 = doc.createElement("WrongAnswer");
        WrongAnswer1.appendChild(doc.createTextNode("3"));
        Element WrongAnswer2 = doc.createElement("WrongAnswer");
        WrongAnswer2.appendChild(doc.createTextNode("PI"));
        Element WrongAnswer3 = doc.createElement("WrongAnswer");
        WrongAnswer3.appendChild(doc.createTextNode("Infinity"));
        Question1.appendChild(QuestionText);
        Question1.appendChild(RightAnswer);
        Question1.appendChild(WrongAnswer1);
        Question1.appendChild(WrongAnswer2);
        Question1.appendChild(WrongAnswer3);

        QuestionText = doc.createElement("QuestionText");
        QuestionText.appendChild(doc.createTextNode("1 Minus 1 is"));
        RightAnswer = doc.createElement("RightAnswer");
        RightAnswer.appendChild(doc.createTextNode("0"));
        WrongAnswer1 = doc.createElement("WrongAnswer");
        WrongAnswer1.appendChild(doc.createTextNode("Infinity minus 1"));
        WrongAnswer2 = doc.createElement("WrongAnswer");
        WrongAnswer2.appendChild(doc.createTextNode("PI"));
        WrongAnswer3 = doc.createElement("WrongAnswer");
        WrongAnswer3.appendChild(doc.createTextNode("2"));
        Question2.appendChild(QuestionText);
        Question2.appendChild(RightAnswer);
        Question2.appendChild(WrongAnswer1);
        Question2.appendChild(WrongAnswer2);
        Question2.appendChild(WrongAnswer3);

        QuestionText = doc.createElement("QuestionText");
        QuestionText.appendChild(doc.createTextNode("Chelsea color is"));
        RightAnswer = doc.createElement("RightAnswer");
        RightAnswer.appendChild(doc.createTextNode("blue"));
        WrongAnswer1 = doc.createElement("WrongAnswer");
        WrongAnswer1.appendChild(doc.createTextNode("red"));
        WrongAnswer2 = doc.createElement("WrongAnswer");
        WrongAnswer2.appendChild(doc.createTextNode("green"));
        WrongAnswer3 = doc.createElement("WrongAnswer");
        WrongAnswer3.appendChild(doc.createTextNode("all above"));
        Question3.appendChild(QuestionText);
        Question3.appendChild(RightAnswer);
        Question3.appendChild(WrongAnswer1);
        Question3.appendChild(WrongAnswer2);
        Question3.appendChild(WrongAnswer3);
*/
//        String text = new StringBuilder()
//                //.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
//                .append("<Category Name=\"MeirBirthday\">\n")
//                    .append("<Question>\n")
//                        .append("<QuestionText>1 plus 1 is</QuestionText>\n")
//                        .append("<RightAnswer>2</RightAnswer>\n")
//                        .append("<WrongAnswer>3</WrongAnswer>\n")
//                        .append("<WrongAnswer>PI</WrongAnswer>\n")
//                        .append("<WrongAnswer>Infinity</WrongAnswer>\n")
//                    .append("</Question>\n")
//                    .append("<Question>\n")
//                        .append("<QuestionText>1 Minus 1 is</QuestionText>\n")
//                        .append("<RightAnswer>0</RightAnswer>\n")
//                        .append("<WrongAnswer>Infinity minus 1</WrongAnswer>\n")
//                        .append("<WrongAnswer>PI</WrongAnswer>\n")
//                        .append("<WrongAnswer>2</WrongAnswer>\n")
//                    .append("</Question>\n")
//                    .append("<Question>\n")
//                        .append("<QuestionText>Chelsea color is</QuestionText>\n")
//                        .append("<RightAnswer>blue</RightAnswer>\n")
//                        .append("<WrongAnswer>red</WrongAnswer>\n")
//                        .append("<WrongAnswer>green</WrongAnswer>\n")
//                        .append("<WrongAnswer>all above</WrongAnswer>\n")
//                    .append("</Question>\n")
//                .append("</Category>\n")
//                .toString();
//
//        doc.createTextNode(text);

       // return doc;
    }

    private boolean getPermission() {
        int grant  = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (grant  != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            // return true if user approved
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void UpdateView() {
        tvCategoryName.setText(categoryName);
        //newCategoryName.setText(categoryName);
        adapter = new ArrayAdapter<String>(this,R.layout.show, selectQuestionOnly(questions));          //what array list will show in  the listView
        questionsView.setAdapter(adapter);                                                                                  //show the questions in the listView
    }

    private List<String> selectQuestionOnly(ArrayList<Question> questions) {
        List<String> questionsOnly = new ArrayList<>();
        for (Question q : questions){
            questionsOnly.add(q.getQuestionString());
        }
        return questionsOnly;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_personal_category);

        UsersService.getUserById(UsersService.getUserId(), new getDataListener() {
            @Override
            public void getData(HashMap<String, Object> user) {
                        userID = (String)user.get("username");
                }
        });

        questionsView = findViewById(R.id.questionsView);
        bAddPrivateCategory = findViewById(R.id.LoadCategoryFile);
        bSavePrivateCategory = findViewById(R.id.SaveCategory);
        tvCategoryName = findViewById(R.id.tvCategoryName);
        //newCategoryName.hide()


        bAddPrivateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!getPermission())
                    return;

                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);

            }
        });

        bSavePrivateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(!ValidateNameNotExist(categoryName))
//                    return;
                int i = 1;
                for (Question q : questions) {
                    addQuestionToDB(q, i++);
                }
                finish();
            }

/*            private boolean ValidateNameNotExist(String categoryName) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Questions");       // Connects to the database - in the userData

                reference.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getKey().equals(categoryName)) {
                            Toast.makeText(CreatePersonalCategory.this , "name already exist. Please change the name ", Toast.LENGTH_SHORT).show();
                            //newCategoryName.show();


                        }


                    }
                });

            }*/

            private void addQuestionToDB(Question q, int index)
            {
                String qNum =String.valueOf(index);

                HashMap<String , Object> map = new HashMap<>();
                if (q.getQuestionString().charAt(q.getQuestionString().length()-1)=='?')
                {
                    map.put("Q", q.getQuestionString());
                }
                else
                {
                    map.put("Q", q.getQuestionString()+'?');
                }
                map.put("XA" , q.getRightAnswer());
                map.put("XB" , q.getWrongAnswers().get(0));
                map.put("XC" , q.getWrongAnswers().get(1));
                map.put("XD" , q.getWrongAnswers().get(2));


                FirebaseDatabase.getInstance().getReference().child("PersonalQuestions").child(userID).child(categoryName).child(qNum).updateChildren(map);



            }
        });

    }
}