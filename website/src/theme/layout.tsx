import Layout from 'dumi-theme-default/src/layout';

export default ({ children, ...props }) => {
  console.log(133424324321321);
  return (
    <Layout {...props}>
      <>
        aaaaaa
        {children}
      </>
    </Layout>
  );
};
