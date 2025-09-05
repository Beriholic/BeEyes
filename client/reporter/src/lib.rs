use anyhow::Result;
use tokio_cron_scheduler::{Job, JobScheduler};

pub async fn start_scheduler() -> Result<()> {
    let sched = JobScheduler::new().await?;

    let job = Job::new("*/1 * * * * *", |uuid, _| {
        println!("I run every second. My Job ID is: {:?}", uuid);
    });

    sched.add(job?).await?;
    sched.start().await?;

    Ok(())
}
