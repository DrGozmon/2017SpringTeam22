# Build and deploy the metalnx-web project as a debian file

# Remove the unzipped folder
rm -rf /home/rhpajers/compilationfolder/metalnx-web/*

# Unzip the newly-uploaded zip file
tar -zxf /home/rhpajers/compilationfolder/metalnx-web.tar.gz --directory /home/rhpajers/compilationfolder/metalnx-web

# Run the Debian builder
#    Change directory to the metalnx-web folder to run the build script
cd /home/rhpajers/compilationfolder/metalnx-web
:

#    Run the build script
WORKSPACE=$(pwd) sh ./packaging/scripts/create_deb_package.sh 1.0 3

# Move the finished Debian file to the built folder
mv /tmp/emc-tmp/emc-metalnx-webapp-1.0-3.deb /home/rhpajers/compilationfolder/built

# Set the ownership as me
chown rhpajers:ssh /home/rhpajers/compilationfolder/built/emc-metalnx-webapp-1.0-3.deb

# Install the package
dpkg -i /home/rhpajers/compilationfolder/built/emc-metalnx-webapp-1.0-3.deb

# Run the installer
python /opt/emc/setup_metalnx.py

# Delete the zipped file
rm /home/rhpajers/compilationfolder/metalnx-web.tar.gz
