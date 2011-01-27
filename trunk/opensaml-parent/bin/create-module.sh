#!/bin/bash

MODULE_NAME=$1
if [ -z "$MODULE_NAME" ]; then
    echo "Module name must be supplied";
    exit 1
fi
MODULE_DIR="opensaml-$MODULE_NAME"

DESCRIPTIVE_NAME=$2
if [ -z "$DESCRIPTIVE_NAME" ]; then
    echo "Descriptive name must be supplied"
    exit 1
fi

if [ ! -e "opensaml-parent" -o  ! -e  "../java-opensaml" ]; then
    echo "You must run this script from within the java-opensaml directory"
    exit 1
fi

#1. Create a directory called opensaml-MODULE_NAME as a child of the java-opensaml
#   project
#

[ ! -e $MODULE_DIR ] && svn mkdir $MODULE_DIR

#2. With in the module directory create the directories:
#   - src/main/java
#   - src/main/resources
#   - src/test/java
#   - src/test/resources

for dir in src/main/java src/main/resources src/test/java src/test/resources; do
    newdir=$MODULE_DIR/$dir
    [ ! -e $newdir ] && svn --parents mkdir $newdir
done

#
#3. Copy the java-opensaml/opensaml-parent/module-settings/pom-template.xml in to 
#   your module directory and edit the following lines:
#   - line 14, replace "DESCRIPTIVE NAME" with a decently descriptive name
#   - line 15, replace "MODULE_NAME"

[ ! -e $MODULE_DIR/pom.xml ] && svn cp opensaml-parent/module-settings/pom-template.xml $MODULE_DIR/pom.xml
perl -p -i -e "s/MODULE_NAME/${MODULE_NAME}/g; s/DESCRIPTIVE_NAME/${DESCRIPTIVE_NAME}/g"  $MODULE_DIR/pom.xml

#
#4. Copy the java-opensaml/opensaml-part/module-settings/eclipse/.project-template in to 
#   your module directory and edit the following lines:
#   - line 3, replace "MODULE_NAME"

[ ! -e $MODULE_DIR/.project ] && svn cp opensaml-parent/module-settings/eclipse/.project-template $MODULE_DIR/.project
perl -p -i -e "s/MODULE_NAME/${MODULE_NAME}/g"  $MODULE_DIR/.project

#
#5. Copy the java-opensaml/opensaml-parent/module-settings/eclipse/.classpath-template in to 
#   your module directory.

[ ! -e $MODULE_DIR/.classpath ] && svn cp opensaml-parent/module-settings/eclipse/.classpath-template $MODULE_DIR/.classpath

#
#6. Add (svn add java-opensaml/opensaml-MODULE_NAME) your module to SVN.
#   
#7. From the java-opensaml directory, run the following SVN command:
#   svn propset svn:externals -F opensaml-parent/module-settings/externals.svn opensaml-MODULE_NAME

svn propset svn:externals -F opensaml-parent/module-settings/externals.svn $MODULE_DIR

#   
#8. From the java-opensaml directory, run the following SVN command:
#   svn propset svn:ignore -F opensaml-parent/module-settings/ignore.svn opensaml-MODULE_NAME

svn propset svn:ignore -F opensaml-parent/module-settings/ignore.svn $MODULE_DIR

#   
#9. Commit (svn commit opensaml-MODULE_NAME -m "LOG MESSAGE") your module to SVN
#
#10. Perform an svn update to pull in the externalized files (set up via step 5)
#
#11. Add module to the idp-parent/pom.xml and commit change to this POM.

echo ""
echo ""
echo "****************************************************"
echo ""
echo "IMPORTANT!"
echo ""
echo "Don't forget to:"
echo " 1) Commit the new module"
echo " 2) Perform an svn update to pull in the externalized files"
echo " 3) Add the module to the opensaml-parent/pom.xml" and commit it
echo ""
echo ""
echo "****************************************************"
