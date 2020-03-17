package com.example.android.firebasetestapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {

    Button buttonRegister, buttonSignIn;
    FirebaseAuth auth; // авторизацмя
    FirebaseDatabase db; // подкл к базе
    DatabaseReference users; // работа с табл внутри бд
    ConstraintLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSignIn = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        auth = FirebaseAuth.getInstance(); // запуск авториз в бд
        db = FirebaseDatabase.getInstance(); // подкл к бд
        users = db.getReference("Users"); // c какой табл работаем
        root = findViewById(R.id.root_element);

        // кнопка зарегистрироваться
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterWindow();
            }
        });
        //кнопка добавить в бд
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignInWindow();
            }
        });
    } // onCreate

    private void showSignInWindow(){
        //класс позв создать всплыв окна
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this); // this - где отобр окно
        dialog.setTitle("Войти"); // заголовок окна
        dialog.setMessage("Введите все данные для входа");
        //получаем шаблон окна регистрации
        LayoutInflater inflater = LayoutInflater.from(this);
        View signInWindow = inflater.inflate(R.layout.sign_in_window, null);
        dialog.setView(signInWindow); // устанавливаем шаблон signInWindow как шабл вспл окна

        // получение данных с полей окна входа
        final MaterialEditText email = signInWindow.findViewById(R.id.emailField);
        final MaterialEditText password = signInWindow.findViewById(R.id.passField);

        //кнопка отмены, закрытия  1 парам - текст, 2 парам - функция которая сработает при клике
        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss(); // закрыть
            }
        });

        //кнопка регистрации, проверит вводимые данные и отправит их в бд
        dialog.setPositiveButton("Войти", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //получаем данные и обрабатываем
                if (TextUtils.isEmpty(email.getText().toString())){ // сли пользователь ничего не ввел ошибка
                    Snackbar.make(root, "Введите вашу почту", Snackbar.LENGTH_SHORT).show(); //вспл окно
                    return; // если ошибка сразу выход из функции
                }
                if (password.getText().toString().length() < 5){
                    Snackbar.make(root, "Введите пароль который более 5 символов", Snackbar.LENGTH_SHORT).show(); //вспл окно
                    return; // если ошибка сразу выход из функции
                }

                //вход по емайл и паролю
                auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() { // при успешном входе
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                startActivity(new Intent(MainActivity.this, MapActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() { // при не успешной авторизации
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root, "Ошибка авторизации. " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.show();
    }

    private void showRegisterWindow() {
        //класс позв создать всплыв окна
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this); // this - где отобр окно
        dialog.setTitle("Зарегистрироваться"); // заголовок окна
        dialog.setMessage("Введите все данные для регистрации");
        //получаем шаблон окна регистрации
        LayoutInflater inflater = LayoutInflater.from(this);
        View registerWindow = inflater.inflate(R.layout.register_window, null);
        dialog.setView(registerWindow); // устанавливаем шаблон registerWindow как шабл вспл окна

        // получение данных с полей окна регистрации
        final MaterialEditText email = registerWindow.findViewById(R.id.emailField);
        final MaterialEditText password = registerWindow.findViewById(R.id.passField);
        final MaterialEditText name = registerWindow.findViewById(R.id.nameField);
        final MaterialEditText phone = registerWindow.findViewById(R.id.phoneField);

        //кнопка отмены, закрытия  1 парам - текст, 2 парам - функция которая сработает при клике
        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss(); // закрыть
            }
        });

        //кнопка регистрации, проверит вводимые данные и отправит их в бд
        dialog.setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //получаем данные и обрабатываем
                if (TextUtils.isEmpty(email.getText().toString())){ // сли пользователь ничего не ввел ошибка
                    Snackbar.make(root, "Введите вашу почту", Snackbar.LENGTH_SHORT).show(); //вспл окно
                    return; // если ошибка сразу выход из функции
                }
                if (TextUtils.isEmpty(name.getText().toString())){ // сли пользователь ничего не ввел ошибка
                    Snackbar.make(root, "Введите ваше имя", Snackbar.LENGTH_SHORT).show(); //вспл окно
                    return;
                }
                if (TextUtils.isEmpty(phone.getText().toString())){ // сли пользователь ничего не ввел ошибка
                    Snackbar.make(root, "Введите ваш телефон", Snackbar.LENGTH_SHORT).show(); //вспл окно
                    return;
                }
                if (password.getText().toString().length() < 5){
                    Snackbar.make(root, "Введите пароль который более 5 символов", Snackbar.LENGTH_SHORT).show(); //вспл окно
                    return; // если ошибка сразу выход из функции
                }

                //Регистрация пользователя
                auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()) // перед лог и пароль
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) { // функция onSuccess вызовется если польз успешно добавлен в бд
                        //создаем обьект который хранит лог пароль имя телефон
                        //сохраняем этот обьект в бд
                        User user = new User();
                        user.setEmail(email.getText().toString());
                        user.setName(name.getText().toString());
                        user.setPass(password.getText().toString());
                        user.setPhone(phone.getText().toString());

                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user)// ключ для пользователя это email
                            .addOnSuccessListener(new OnSuccessListener<Void>() { // обраб соб сработает когда успешно добавлен пользователь
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Snackbar.make(root, "Пользователь добавлен", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                    }
                });
            }
        });
        dialog.show();
    }
}
