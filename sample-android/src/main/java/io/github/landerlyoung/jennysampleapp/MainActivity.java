/**
 * Copyright 2016 landerlyoung@gmail.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.landerlyoung.jennysampleapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class MainActivity extends AppCompatActivity {

    private TextView mTextView;
    private NativeDrawable mNativeDrawable;

    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mNativeDrawable = new NativeDrawable();
        ImageView bg = findViewById(R.id.image);
        bg.setImageDrawable(mNativeDrawable);
        bg.setOnClickListener(v -> {
            bg.invalidate();
            mNativeDrawable.onClick();
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ComputeIntensiveClass.runJniHelperTest();

        final ComputeIntensiveClass nativeClass = new ComputeIntensiveClass();
        mTextView = findViewById(R.id.text);
        mTextView.setOnClickListener(v -> mTextView.setText(null));
        mTextView.setText("1 + 2 = " + nativeClass.addInNative(1, 2) + "\n");
        mTextView.append(ComputeIntensiveClass.greet());
        ComputeIntensiveClass.testOverload();
        ComputeIntensiveClass.testOverload(0);

        FloatingActionButton fab = findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testComputeIntensiveClass(view);
            }

            private void testComputeIntensiveClass(View view) {
                ComputeIntensiveClass.NestedNativeClass nestedNativeClass = new ComputeIntensiveClass.NestedNativeClass();
                long handle = nestedNativeClass.nativeInit();
                nestedNativeClass.testOverload();
                nestedNativeClass.testOverload(nestedNativeClass);
                nestedNativeClass.nativeRelease(handle);
                nestedNativeClass.one("hello");
                nativeClass.computeThenCallback(new Callback() {
                    @Override
                    public void onJobDone(boolean success, String result) {
                        toast("success=" + success + " result=" + result
                                + "\ncount=" + count + " obj==this = " + (lock == this) + "\n");
                    }

                    @Override
                    public void onJobProgress(long progress) {
                        toast("onJobProgress = " + progress + " lock = " + lock);
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

        EXECUTOR.execute(this::testNativeHttpGet);
    }

    private void testNativeHttpGet() {
        String url = "https://jsonplaceholder.typicode.com/todos/1";
        runOnUiThread(() -> toast("http get: " + url + "\n"));
        final String json = ComputeIntensiveClass.httpGet(url);
        runOnUiThread(() -> toast("http got\n" + json));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNativeDrawable.release();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        mTextView.append("\n");
        mTextView.append(msg);
    }

}
