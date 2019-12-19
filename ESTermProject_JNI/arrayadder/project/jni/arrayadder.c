#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <termios.h>
#include <sys/mman.h>
#include <errno.h>
#include <string.h>

//1 on 0 off
jint Java_ac_kr_kgu_esproject_ArrayAdderActivity_BuzzerControl(JNIEnv* env, jobject thiz, jint value) {
	int fd, ret;
	int data = value;
	fd = open("/dev/arrayadder", O_WRONLY);

	if( fd < 0 )
		return -errno;

	ret = ioctl(fd, data, NULL, NULL);
	close(fd);

	if( ret == 1 )
		return 0;

	return -1;
}


void Java_ac_kr_kgu_esproject_ArrayAdderActivity_SegmentControl(JNIEnv* env, jobject thiz, jbyte data0, jbyte data1) {
	int fd, ret;
	char datas[2];
	//jchar * array = (*env)->GetCharArrayElements(env, data, 0);

	datas[0] = data0;
	datas[1] = data1;

	fd = open("/dev/arrayadder", O_RDWR | O_SYNC);

	if( fd != -1 ) {
		ret = write(fd, datas, 2);
		close(fd);
	} 
	else {
		exit(1);
	}

	//(*env)->ReleaseCharArrayElements(env, data, array, 0);
	//(*env)->DeleteLocalRef(env, data);
	//(*env)->DeleteLocalRef(env, array);
}


//if answer is exact return 1, or 0  second elem is the answer
jintArray Java_ac_kr_kgu_esproject_ArrayAdderActivity_checkAnswer(JNIEnv* env, jobject thiz, jintArray datas) {
	int i, sum  = 0;
	jintArray newArray = (*env)->NewIntArray(env, 2);
	
	jsize len = (*env)->GetArrayLength(env, datas);
	jint * array = (*env)->GetIntArrayElements(env, datas, 0);
	jint * new_array = (*env)->GetIntArrayElements(env, newArray, 0);
	
	for(i = 0; i < len - 1; i++) {
		sum += array[i];
	}

	if( sum == array[len - 1] ) {
		new_array[0] = 1;
		new_array[1] = sum;
	}
	else {
		new_array[0] = 0;
		new_array[1] = sum;
	}
	
	(*env)->ReleaseIntArrayElements(env, datas, array, 0);

	return newArray;
}
