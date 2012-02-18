package jenkins.plugins.notifysend;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.BallColor;
import hudson.model.Hudson;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Notifier;

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Map;

public class NotifysendNotifier extends Notifier {

    @DataBoundConstructor
    public NotifysendNotifier() {
    }


    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) 
    {
      BallColor color = build.getResult().color;
      String chemin   = Hudson.getInstance().getRootPath().toString() + "/war/images/48x48/";
      String image    = chemin + color.getImage();

      try{
        Map<String, String> liste = build.getBuildVariables();
        String branch  = liste.get("BRANCH");
        String result  = build.getResult().toString();
        String message = "Job " + build.getProject().getName()  + " terminé (branche : " +  branch + ", build " + build.number + ", etat : " + result + ")";
        Runtime.getRuntime().exec(new String[] { "notify-send", "--icon=" + image, "Jenkins local", message } );
      }catch (IOException e) {
      }

      return true;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link NotifiersendPublisher}. Used as a singleton.
     */
    @Extension // this marker indicates Hudson that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Send notification via notify-send";
        }
    }

}

