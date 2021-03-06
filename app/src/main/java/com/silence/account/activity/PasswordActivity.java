package com.silence.account.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.silence.account.R;
import com.silence.account.utils.MD5Encrypt;
import com.silence.account.utils.T;
import com.silence.account.view.PasswordEditText;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class PasswordActivity extends BaseActivity {

    @Bind(R.id.et_modify_newpass)
    PasswordEditText mEtModifyNewPass;
    @Bind(R.id.et_modify_oldpass)
    PasswordEditText mEtModifyOldpass;

    @Override
    public void initView() {
        disPlayBack(true);
        setActionTitle("修改密码");
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_modify_pass)
    public void onClick() {
        final String newPass = mEtModifyNewPass.getText().toString().trim();
        final String oldPass = mEtModifyOldpass.getText().toString().trim();
        if (!TextUtils.isEmpty(newPass) && !TextUtils.isEmpty(oldPass)) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    BmobQuery<BmobUser> query = new BmobQuery<>();
                    final String secretOldPass = MD5Encrypt.MD5Encode(oldPass);
                    query.addWhereEqualTo("password", secretOldPass);
                    query.findObjects(PasswordActivity.this, new FindListener<BmobUser>() {
                        @Override
                        public void onSuccess(List<BmobUser> object) {
                            if (object.size() > 0) {
                                BmobUser.updateCurrentUserPassword(PasswordActivity.this,
                                        secretOldPass, MD5Encrypt.MD5Encode(newPass), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        T.showShort(getApplicationContext(), "密码修改成功");
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(int code, String msg) {
                                        T.showShort(getApplicationContext(), "密码修改失败");
                                    }
                                });
                            } else {
                                T.showShort(getApplicationContext(), "旧密码错误");
                            }
                        }

                        @Override
                        public void onError(int code, String msg) {
                            T.showShort(getApplicationContext(), "请检查网络连接");
                        }
                    });
                    return null;
                }
            }.execute();
        } else {
            T.showShort(this, "请输入完整密码");
        }
    }
}
