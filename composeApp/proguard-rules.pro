-ignorewarnings
-dontwarn kotlinx.coroutines.**
-dontwarn kotlin.**
-dontwarn java.awt.**
-dontwarn javax.swing.**

-dontwarn com.ibm.icu.**
-dontwarn sun.misc.**

-keep class com.ibm.icu.** { *; }
-keepattributes SourceFile,LineNumberTable

# keep smali as reflection is used for highlighting
-keep class com.android.tools.smali.** { *; }

# keep JADX as it crashes if not
-keep class jadx.** { *; }