package com.monash.app.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.monash.app.R;
import com.monash.app.utils.ConfigUtil;
import com.monash.app.utils.EventUtil;
import com.monash.app.utils.HttpUtil;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProfileActivity extends BaseActivity implements CalendarDatePickerDialogFragment.OnDateSetListener{

    @BindView(R.id.tv_studID) TextView tvUserID;
    @BindView(R.id.tv_email) TextView tvUserEmail;
    @BindView(R.id.tv_studentName) TextView tvUserFullName;
    @BindView(R.id.tv_birthDate) TextView tvUserBirthDate;
    @BindView(R.id.rg_gender) RadioGroup rgUserGenderGroup;
    @BindView(R.id.rb_secret)RadioButton rbSecret;
    @BindView(R.id.rb_female) RadioButton rbFemale;
    @BindView(R.id.rb_male) RadioButton rbMale;
    @BindView(R.id.rg_studyMode) RadioGroup rgStudyModeGroup;
    @BindView(R.id.rb_fullTime) RadioButton rbFullTime;
    @BindView(R.id.rb_partTime) RadioButton rbPartTime;
    @BindView(R.id.et_nationality) EditText etNationality;
    @BindView(R.id.et_language) EditText etLanguage;
    @BindView(R.id.sp_course) Spinner spCourse;
    @BindView(R.id.et_address) EditText etAddress;
    @BindView(R.id.et_suburb) EditText etSuburb;
    @BindView(R.id.et_currentJob) EditText etCurrentJob;
    @BindView(R.id.et_favoriteMovie) EditText etFavMovie;
    @BindView(R.id.sp_favoriteSport) Spinner spFavSport;
    @BindView(R.id.sp_favoriteUnit) Spinner spFavUnit;
    @BindView(R.id.tv_subscribeDate) TextView tvSubscribeDate;

    private String gender;
    private String course;
    private String studyMode;
    private String address;
    private String suburb;
    private String nationality;
    private String language;
    private String favSport;
    private String favUnit;
    private String favMovie;
    private String birthDate;
    private String subscriptionDate;


    private static String CHANGE_BIRTH_DATE = "CHANGE_BIRTH_DATE";
    private static String CHANGE_SUBSCRIBE_DATE = "CHANGE_SUBSCRIBE_DATE";
    private boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        initUserInfo();
        initListener();
    }

    private void init(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    private void initListener() {
        rgUserGenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                switch (id){
                    case R.id.rb_secret:
                        gender = rbSecret.getText().toString();
                        break;
                    case R.id.rb_female:
                        gender = rbFemale.getText().toString();
                        break;
                    case R.id.rb_male:
                        gender = rbMale.getText().toString();
                        break;
                }
            }
        });

        rgStudyModeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                switch (id){
                    case R.id.rb_fullTime:
                        studyMode = rbFullTime.getText().toString();
                        break;
                    case R.id.rb_partTime:
                        studyMode = rbPartTime.getText().toString();
                        break;
                }
            }
        });

    }

    private void initUserInfo(){
        if (user == null){
            Logger.d("user is null");
            return;
        }
        tvUserID.setText(String.valueOf(user.getStudID()));
        tvUserEmail.setText(user.getEmail());
        String userName = user.getSurName() + " " + user.getFirstName();
        tvUserFullName.setText(userName);
        String birthDateTemp = handleDate(user.getBirthDate());
        if (birthDateTemp != null)
            tvUserBirthDate.setText(birthDateTemp);
        // 日期改成String类型的。
        birthDate = changeDateToString(user.getBirthDate());

        gender = user.getGender();
        if (gender != null){
            if (gender.equals("male")){
                rgUserGenderGroup.check(rbMale.getId());
            } else if (gender.equals("female")){
                rgUserGenderGroup.check(rbFemale.getId());
            } else {
                rgUserGenderGroup.check(rbSecret.getId());
            }
        }

        studyMode = user.getStudyMode();
        Logger.d(studyMode);
        if (studyMode != null){
            if(studyMode.equals("full-time")){
                rgStudyModeGroup.check(rbFullTime.getId());
            } else {
                rgStudyModeGroup.check(rbPartTime.getId());
            }
        }
        String[] courses = getResources().getStringArray(R.array.courses);
        String course = user.getCourse();
        if (course != null){
            for (int i = 1; i < courses.length; i++){
                if (course.equals(courses[i])){
                    spCourse.setSelection(i, true);
                    break;
                }
            }
        } else {
            spCourse.setSelection(0, true);
        }
        String nationality = user.getNationality();
        if (nationality != null){
            etNationality.setText(captureFirstLetter(nationality));
            etNationality.setFocusable(false);
            etNationality.setFocusableInTouchMode(false);
        }
        String language = user.getLanguage();
        if (language != null){
            etLanguage.setText(captureFirstLetter(language));
            etLanguage.setFocusable(false);
            etLanguage.setFocusableInTouchMode(false);
        }
        etAddress.setText(user.getAddress());
        etSuburb.setText(user.getSuburb());
        if (!TextUtils.isEmpty(user.getCurrentJob())){
            etCurrentJob.setText(user.getCurrentJob());
        }
        etFavMovie.setText(user.getFavMovie());
        String[] sports = getResources().getStringArray(R.array.sports);
        String[] units = getResources().getStringArray(R.array.units);
        String favSport = user.getFavSport();
        String favUnit = user.getFavUnit();
        if (favSport != null){
            favSport = favSport.toLowerCase();
            for (int i = 1; i < sports.length; i++){
                if (favSport.equals(sports[i].toLowerCase())){
                    spFavSport.setSelection(i, true);
                    break;
                }
            }
        } else {
            spFavSport.setSelection(0, true);
        }
        if (favUnit != null){
            for (int i = 1; i < units.length; i++){
                if (favUnit.equals(units[i])){
                    spFavUnit.setSelection(i, true);
                }
            }
        } else {
            spFavUnit.setSelection(0, true);
        }
        etFavMovie.setText(user.getFavMovie());
        String subscriptionDateTemp = handleDate(user.getSubscriptionDate());
        if (subscriptionDateTemp != null){
            tvSubscribeDate.setText(subscriptionDateTemp);
        }
        subscriptionDate = changeDateToString(user.getSubscriptionDate());
        isChanged = false;
    }

    private String changeDateToString(Date date) {
        if (date == null){
            return "Please input birthday";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        String dateStr = String.format(getResources().getString(R.string.date_format),
                String.valueOf(year), String.valueOf(month), String.valueOf(day));
        return dateStr;
    }

    private String handleDate(Date date){
        if (date != null)
            return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
        else
            return null;

    }

    private void showTipDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tip");
        builder.setMessage("Please input your " + text);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }


    private String captureFirstLetter(String str){
        char[] arr = str.toCharArray();
        if (arr[0] >= 'a') {
            arr[0] -= 32;
        }
        return String.valueOf(arr);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_profile;
    }

    @OnClick(R.id.btn_saveProfile)
    void saveProfileChange(){
        if (checkChangedInfo() && user != null){
            JSONObject jsonObject = packageJSON();
            try {
                if (jsonObject != null)
                    HttpUtil.getInstance().post(ConfigUtil.POST_USER_UPDATE + user.getStudID(), ConfigUtil.EVENT_EDIT_USER_INFO, jsonObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject packageJSON() {
        JSONObject jsonObject = new JSONObject();
        if (user != null){
            try {
                jsonObject.put("firstName", user.getFirstName());
                jsonObject.put("surName", user.getSurName());
                jsonObject.put("email", user.getEmail());
                jsonObject.put("password", user.getPassword());
                jsonObject.put("gender", gender);
                jsonObject.put("course", course);
                jsonObject.put("studyMode", studyMode);
                jsonObject.put("address", address);
                jsonObject.put("suburb", suburb);
                jsonObject.put("nationality", nationality);
                jsonObject.put("language", language);
                jsonObject.put("favSport", favSport);
                jsonObject.put("favUnit", favUnit);
                jsonObject.put("favMovie", favMovie);
                jsonObject.put("currentJob", etCurrentJob.getText());
                jsonObject.put("birthDate", birthDate);
                jsonObject.put("subscriptionDate", subscriptionDate);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        } else {
            return null;
        }
    }

    private boolean checkChangedInfo() {
        if (TextUtils.isEmpty(birthDate)){
            showTipDialog("BirthDay");
            return false;
        }
        if (TextUtils.isEmpty(gender)){
            showTipDialog("Gender");
            return false;
        }
        if (TextUtils.isEmpty(studyMode)){
            showTipDialog("StudyMode");
            return false;
        }
        if (course.length() > 10){
            showTipDialog("Course");
            return false;
        }
        if (TextUtils.isEmpty(nationality)){
            showTipDialog("Nationality");
            return false;
        }
        if (TextUtils.isEmpty("Language")){
            showTipDialog("Language");
            return false;
        }
        if (TextUtils.isEmpty(address)){
            showTipDialog("Address");
            return false;
        }
        if (TextUtils.isEmpty(suburb)){
            showTipDialog("Suburb");
            return false;
        }
        if (TextUtils.isEmpty(favMovie)){
            showTipDialog("Favorite Movie");
            return false;
        }
        if (TextUtils.isEmpty(favSport)){
            showTipDialog("Favorite Sport");
            return false;
        }
        if (TextUtils.isEmpty(favUnit)){
            showTipDialog("Favorite Unit");
            return false;
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void saveUserInfoSuccess(EventUtil eventUtil){
        if (eventUtil.getEventType() == ConfigUtil.EVENT_EDIT_USER_INFO){
            int code = eventUtil.getResponseCode();
            if (code == 200) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Connect Server error!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnTextChanged(R.id.et_nationality)
    public void onNationalityChanged(){
        if (user != null) {
            user.setNationality(captureFirstLetter(etNationality.getText().toString()));
            nationality = captureFirstLetter(etNationality.getText().toString());
            isChanged = true;
        }
    }

    @OnTextChanged(R.id.et_language)
    public void onLanguageChanged(){
        if (user != null) {
            user.setLanguage(captureFirstLetter(etLanguage.getText().toString()));
            language = captureFirstLetter(etLanguage.getText().toString());
            isChanged = true;
        }
    }

    @OnTextChanged(R.id.et_address)
    public void onAddressChanged(){
        if (user != null) {
            user.setAddress(etAddress.getText().toString());
            address = etAddress.getText().toString();
            isChanged = true;
        }
    }

    @OnTextChanged(R.id.et_suburb)
    public void onSuburbChanged(){
        if (user != null) {
            user.setSuburb(etSuburb.getText().toString());
            suburb = etSuburb.getText().toString();
            isChanged = true;
        }
    }

    @OnTextChanged(R.id.et_currentJob)
    public void onCurrentJobChanged(){
        if (user != null) {
            user.setCurrentJob(etCurrentJob.getText().toString());
            isChanged = true;
        }
    }

    @OnTextChanged(R.id.et_favoriteMovie)
    public void onMovieChanged(){
        if (user != null) {
            user.setFavMovie(etFavMovie.getText().toString());
            favMovie = etFavMovie.getText().toString();
            isChanged = true;
        }
    }

    @OnItemSelected(R.id.sp_course)
    public void onCourseItemSelected(){
        String course_temp = spCourse.getSelectedItem().toString();
        if (course_temp != null && user != null){
            Logger.d(course_temp);
            user.setCourse(course_temp);
            course = course_temp;
            isChanged = true;
        }
    }

    @OnItemSelected(R.id.sp_favoriteSport)
    public void onSportItemSelected(){
        String sport = spFavSport.getSelectedItem().toString();
        if (sport != null && user != null) {
            Logger.d(sport);
            user.setFavSport(spFavSport.getSelectedItem().toString());
            favSport = sport;
            isChanged = true;
        }
    }

    @OnItemSelected(R.id.sp_favoriteUnit)
    public void onUnitSelected(){
        String unit = spFavUnit.getSelectedItem().toString();
        if (unit != null && user != null){
            Logger.d(unit);
            user.setFavUnit(spFavUnit.getSelectedItem().toString());
            favUnit = unit;
            isChanged = true;
        }
    }

    @OnClick(R.id.tv_birthDate)
    void changeBirthDate(){
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setOnDateSetListener(this)
                .setDoneText("OK")
                .setCancelText("Cancel");
        cdp.show(getSupportFragmentManager(), CHANGE_BIRTH_DATE);
    }


    @OnClick(R.id.tv_subscribeDate)
    void changeSubscribeDate(){
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setOnDateSetListener(this)
                .setDoneText("OK")
                .setCancelText("Cancel");
        cdp.show(getSupportFragmentManager(), CHANGE_SUBSCRIBE_DATE);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        Date date = calendar.getTime();
        String tag = dialog.getTag();
        isChanged = true;
        if (tag.equals(CHANGE_BIRTH_DATE)) {
            tvUserBirthDate.setText(handleDate(date));

            birthDate = String.format(getResources().getString(R.string.date_format),
                    String.valueOf(year), String.valueOf(monthOfYear + 1), String.valueOf(dayOfMonth));

        } else {
            tvSubscribeDate.setText(handleDate(date));

            subscriptionDate = String.format(getResources().getString(R.string.date_format),
                    String.valueOf(year), String.valueOf(monthOfYear + 1), String.valueOf(dayOfMonth));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
