package org.openmicroscopy.release.gradle.internal;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.internal.Cast;
import org.gradle.util.ConfigureUtil;
import org.openmicroscopy.release.gradle.ReleaseExtension;
import org.openmicroscopy.release.gradle.ScopeOptions;
import org.openmicroscopy.release.gradle.SnapshotOptions;
import org.openmicroscopy.release.gradle.StageOptions;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;


public class DefaultReleaseExtension implements ReleaseExtension {

    private final Project project;
    private final ObjectFactory objects;

    private DefaultScopeOptions scopeOptions;
    private DefaultStageOptions stageOptions;

    public DefaultReleaseExtension(Project project) {
        this.project = project;
        this.objects = project.getObjects();

        this.scopeOptions =
                objects.newInstance(DefaultScopeOptions.class, project, Collections.emptyMap());

        this.stageOptions =
                objects.newInstance(DefaultSnapshotOptions.class, project, Collections.emptyMap());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNormal(ReleaseExtension ext) {
        project.getLogger().warn("release.normal = scopeFromProp() is deprecated and will be removed in 1.0.0." +
                " Call release.scopeFromProp() instead.");
        // no op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPreRelease(ReleaseExtension ext) {
        project.getLogger().warn("release.preRelease = stageFromProp() or snapshotFromProp() is deprecated " +
                "and will be removed in 1.0.0. Call release.stageFromProp() or release.snapshotFromProp() instead.");
        // no op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scopeOptions(Map<String, ?> scopeOpts) {
        this.scopeOptions = objects.newInstance(DefaultScopeOptions.class, project, scopeOpts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scopeOptions(Action<? super ScopeOptions> scopeOpts) {
        scopeOpts.execute(this.scopeOptions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scopeOptions(Closure scopeOpts) {
        useStages(ConfigureUtil.configureUsing(scopeOpts));
    }

    @Override
    public void useStages() {
        useStages(Collections.emptyMap());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void useStages(Map<String, ?> stageOpts) {
        useStageOptions(objects.newInstance(DefaultStageOptions.class, project, stageOpts), null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void useStages(Action<? super StageOptions> stageOpts) {
        useStageOptions(objects.newInstance(DefaultStageOptions.class, project, Collections.emptyMap()), stageOpts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void useStages(Closure stageOpts) {
        useStages(ConfigureUtil.configureUsing(stageOpts));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void useSnapshot() {
        useSnapshot(Collections.emptyMap());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void useSnapshot(Map<String, ?> snapshotOpts) {
        useStageOptions(objects.newInstance(DefaultSnapshotOptions.class, project, snapshotOpts), null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void useSnapshot(Action<? super SnapshotOptions> snapshotOpts) {
        useStageOptions(objects.newInstance(DefaultSnapshotOptions.class, project, Collections.emptyMap()), snapshotOpts);
    }

    @Override
    public void useSnapshot(Closure stageOpts) {
        useSnapshot(ConfigureUtil.configureUsing(stageOpts));
    }

    /**
     * @return the scope options created by dsl configuration
     */
    public DefaultScopeOptions getScopeOptions() {
        return scopeOptions;
    }

    /**
     * @return the stage options created by dsl configuration
     */
    public DefaultStageOptions getStageOptions() {
        return stageOptions;
    }

    /**
     * @param stageOptions
     * @param stageOptionsConfigure
     * @param <T>
     * @return
     */
    private <T extends StageOptions> DefaultStageOptions useStageOptions(
            DefaultStageOptions stageOptions, @Nullable Action<? super T> stageOptionsConfigure) {
        if (stageOptions == null) {
            throw new IllegalArgumentException("StageOptions is null!");
        }

        this.stageOptions = stageOptions;

        if (stageOptionsConfigure != null) {
            stageOptionsConfigure.execute(Cast.<T>uncheckedCast(this.stageOptions));
        }

        return this.stageOptions;
    }

}