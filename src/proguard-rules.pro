# Add any ProGuard configurations specific to this
# extension here.

-keep public class com.sumit.dalleimageedit.DalleImageEdit {
    public *;
 }
-keeppackagenames gnu.kawa**, gnu.expr**

-optimizationpasses 4
-allowaccessmodification
-mergeinterfacesaggressively

-repackageclasses 'com/sumit/dalleimageedit/repack'
-flattenpackagehierarchy
-dontpreverify
