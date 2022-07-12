/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_genomicsdb_reader_GenomicsDBQuery */

#ifndef _Included_org_genomicsdb_reader_GenomicsDBQuery
#define _Included_org_genomicsdb_reader_GenomicsDBQuery
#ifdef __cplusplus
extern "C" {
#endif
#undef org_genomicsdb_reader_GenomicsDBQuery_defaultSegmentSize
#define org_genomicsdb_reader_GenomicsDBQuery_defaultSegmentSize 10485760LL
/*
 * Class:     org_genomicsdb_reader_GenomicsDBQuery
 * Method:    jniInitialize
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_genomicsdb_reader_GenomicsDBQuery_jniInitialize
  (JNIEnv *, jclass);

/*
 * Class:     org_genomicsdb_reader_GenomicsDBQuery
 * Method:    jniVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_genomicsdb_reader_GenomicsDBQuery_jniVersion
  (JNIEnv *, jclass);

/*
 * Class:     org_genomicsdb_reader_GenomicsDBQuery
 * Method:    jniConnect
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;J)J
 */
JNIEXPORT jlong JNICALL Java_org_genomicsdb_reader_GenomicsDBQuery_jniConnect
  (JNIEnv *, jclass, jstring, jstring, jstring, jobject, jlong);

/*
 * Class:     org_genomicsdb_reader_GenomicsDBQuery
 * Method:    jniConnectJSON
 * Signature: (Ljava/lang/String;Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_org_genomicsdb_reader_GenomicsDBQuery_jniConnectJSON
  (JNIEnv *, jclass, jstring, jstring);

/*
 * Class:     org_genomicsdb_reader_GenomicsDBQuery
 * Method:    jniConnectPBBinaryString
 * Signature: ([BLjava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_org_genomicsdb_reader_GenomicsDBQuery_jniConnectPBBinaryString
  (JNIEnv *, jclass, jbyteArray, jstring);

/*
 * Class:     org_genomicsdb_reader_GenomicsDBQuery
 * Method:    jniDisconnect
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_genomicsdb_reader_GenomicsDBQuery_jniDisconnect
  (JNIEnv *, jclass, jlong);

/*
 * Class:     org_genomicsdb_reader_GenomicsDBQuery
 * Method:    jniQueryVariantCalls
 * Signature: (JLjava/lang/String;Ljava/util/List;Ljava/util/List;)Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_org_genomicsdb_reader_GenomicsDBQuery_jniQueryVariantCalls
  (JNIEnv *, jclass, jlong, jstring, jobject, jobject);

/*
 * Class:     org_genomicsdb_reader_GenomicsDBQuery
 * Method:    jniGenerateVCF
 * Signature: (JLjava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V
 */
JNIEXPORT void JNICALL Java_org_genomicsdb_reader_GenomicsDBQuery_jniGenerateVCF
  (JNIEnv *, jclass, jlong, jstring, jobject, jobject, jstring, jstring, jstring, jstring, jboolean);

/*
 * Class:     org_genomicsdb_reader_GenomicsDBQuery
 * Method:    jniGenerateVCF1
 * Signature: (JLjava/lang/String;Ljava/lang/String;Z)V
 */
JNIEXPORT void JNICALL Java_org_genomicsdb_reader_GenomicsDBQuery_jniGenerateVCF1
  (JNIEnv *, jclass, jlong, jstring, jstring, jboolean);

#ifdef __cplusplus
}
#endif
#endif
