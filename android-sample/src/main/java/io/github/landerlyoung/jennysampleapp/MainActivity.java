/**
 * Copyright 2016 landerlyoung@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.landerlyoung.jennysampleapp;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ComputeIntensiveClass nativeClass = new ComputeIntensiveClass();
        mTextView = (TextView) findViewById(R.id.text);
        mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTextView.setText("1 + 2 = " + nativeClass.addInNative(1, 2) + "\n");
        mTextView.append(ComputeIntensiveClass.greet());


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hello", Snackbar.LENGTH_SHORT)
                        .show();
                ComputeIntensiveClass.NestedNativeClass nestedNativeClass = new ComputeIntensiveClass.NestedNativeClass();
                long handle = nestedNativeClass.nativeInit();
                nestedNativeClass.nativeRelease(handle);
                nestedNativeClass.one("hello");
                nativeClass.computeThenCallback(new Callback() {
                    @Override
                    public void onJobDone(boolean success, String result) {
                        toast("success=" + success + " result=" + result
                        + "\ncount=" + count + " obj==this = " + (lock == this));
                    }

                    @Override
                    public void onJobProgress(long progress) {
                        toast("onJobProgress = " + progress + " lock = " + System.identityHashCode(lock));
                    }

                    @Override
                    public void onJobStart() {
                        toast("onJobStart");
                    }

                    @Override
                    public int prepareRun() {
                        return 0;
                    }
                });
            }
        });

        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                final String json = ComputeIntensiveClass.httpGet("https://jsonplaceholder.typicode.com/todos/1");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toast(json);
                    }
                });
            }
        });

    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        mTextView.append("\n");
        mTextView.append(msg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void test(Callback callback) {
        int a = Callback.COMPILE_CONSTANT_INT;
        int b = callback.count;
        int c = a + b;
    }
}
