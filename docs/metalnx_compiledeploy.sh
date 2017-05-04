# Build and deploy the metalnx-web project as a debian file
touch /checkroot 2>/dev/null
uid=`stat -c "%u" /checkroot 2>/dev/null`

if [ "$uid" = "0" ]
then
    echo "Root user"
else
    echo "Not a root user"
	exit 1
fi

username='rhpajers'

# Unzip the newly-uploaded zip file
tar -zxf /home/$username\/compilationfolder/metalnx-web.tar.gz --directory /home/$username\/compilationfolder/metalnx-web

# Run the Debian builder
#    Change directory to the metalnx-web folder to run the build script
cd /home/$username\/compilationfolder/metalnx-web
:

#    Run the build script
# WORKSPACE=$(pwd) sh ./packaging/scripts/create_deb_package.sh 1.1 3
# EMC's entire build script
WORKSPACE=/home/$username\/compilationfolder/metalnx-web
TMP_DIR=/tmp/emc-tmp
PROJECT_NAME="emc-metalnx-webapp"
PROJECT_VERSION="1.1"
PROJECT_RELEASE="3"
rm -rf $TMP_DIR
mkdir -p $TMP_DIR/$PROJECT_NAME-$PROJECT_VERSION-$PROJECT_RELEASE/tmp/emc-tmp
mkdir -p $TMP_DIR/$PROJECT_NAME-$PROJECT_VERSION-$PROJECT_RELEASE/opt/emc/ldap
mkdir -p $TMP_DIR/$PROJECT_NAME-$PROJECT_VERSION-$PROJECT_RELEASE/opt/emc/lib
cp $WORKSPACE/src/emc-metalnx-web/target/emc-metalnx-web.war $TMP_DIR/$PROJECT_NAME-$PROJECT_VERSION-$PROJECT_RELEASE/tmp/emc-tmp/
cp -r $WORKSPACE/packaging/scripts/* $TMP_DIR/$PROJECT_NAME-$PROJECT_VERSION-$PROJECT_RELEASE/opt/emc/
cp $WORKSPACE/contrib/ldap/* $TMP_DIR/$PROJECT_NAME-$PROJECT_VERSION-$PROJECT_RELEASE/opt/emc/ldap/
cp -r $WORKSPACE/packaging/deb/DEBIAN $TMP_DIR/$PROJECT_NAME-$PROJECT_VERSION-$PROJECT_RELEASE/
rm $TMP_DIR/$PROJECT_NAME-$PROJECT_VERSION-$PROJECT_RELEASE/opt/emc/create_rpm_package.sh
rm $TMP_DIR/$PROJECT_NAME-$PROJECT_VERSION-$PROJECT_RELEASE/opt/emc/create_deb_package.sh
cd $TMP_DIR/$PROJECT_NAME-$PROJECT_VERSION-$PROJECT_RELEASE/opt/emc/
cp /home/rhpajers/test-connection.jar .
cd -
chmod -R 755 $TMP_DIR/$PROJECT_NAME-$PROJECT_VERSION-$PROJECT_RELEASE/DEBIAN
sed -i "s/{{VERSION-NUMBER}}/$PROJECT_VERSION/" $TMP_DIR/$PROJECT_NAME-$PROJECT_VERSION-$PROJECT_RELEASE/DEBIAN/control
sed -i "s/{{BUILD-NUMBER}}/$PROJECT_RELEASE/" $TMP_DIR/$PROJECT_NAME-$PROJECT_VERSION-$PROJECT_RELEASE/DEBIAN/control
dpkg-deb --build $TMP_DIR/$PROJECT_NAME-$PROJECT_VERSION-$PROJECT_RELEASE


# Move the finished Debian file to the built folder
mv /tmp/emc-tmp/emc-metalnx-webapp-1.1-3.deb /home/$username\/compilationfolder/built

# Set the ownership as me
chown $username\:ssh /home/$username\/compilationfolder/built/emc-metalnx-webapp-1.1-3.deb

# Install the package
dpkg -i /home/$username\/compilationfolder/built/emc-metalnx-webapp-1.1-3.deb

echo -e "\033[0;32mHit Enter for every option: (Yes, Yes, No)"

# Run the installer
python /opt/emc/setup_metalnx.py

# If everything is successful, delete folders no longer needed
success=`echo $?`
if [ "$success" = "0" ]
then
    echo "Successful, now deleting old files"
	
	# Delete the zipped file
	rm /home/$username\/compilationfolder/metalnx-web.tar.gz
	
	# Remove the older, unzipped folder
	rm -rf /home/$username\/compilationfolder/metalnx-web/*
	
else
    echo "Error detected, not deleting the folder"
	echo "This script can be run again"
fi

