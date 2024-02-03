import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import '@umijs/max';
const Footer: React.FC = () => {
  const defaultMessage = '蚂蚁集团体验技术部出品';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: '鱼皮接口',
          title: '小ray接口',
          href: 'https://pro.ant.design',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <GithubOutlined />,
          href: 'https://github.com/nastyray',
          blankTarget: true,
        },
        {
          key: '鱼皮接口',
          title: 'NastyRay',
          href: 'https://github.com/nastyray',
          blankTarget: true,
        },
      ]}
    />
  );
};
export default Footer;
