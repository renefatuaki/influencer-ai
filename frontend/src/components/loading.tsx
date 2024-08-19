import classes from './loading.module.css';

export default function Loading() {
  return (
    <div className={`${classes.container} flex flex-col justify-self-center`}>
      <div className={classes.dot}></div>
      <div className={classes.dot}></div>
      <div className={classes.dot}></div>
      <div className={classes.dot}></div>
      <div className={classes.dot}></div>
      <div className={classes.dot}></div>
      <div className={classes.dot}></div>
      <div className={classes.dot}></div>
    </div>
  );
}
