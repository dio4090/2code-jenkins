import json
import boto3
import argparse


class AwsSecretsRetriever:

    def get_secret(self):

        try:

            data_list = []
            CONTAINER_NAME_KEY = "CONTAINER_NAME"
            DOCKER_IMAGE_KEY = "DOCKER_IMAGE"

            args_data = vars(parser.parse_args())

            client = boto3.client('secretsmanager',
                                  aws_access_key_id=args_data['access_key_id'],
                                  aws_secret_access_key=args_data['secret_access_key_id'],
                                  region_name=args_data['region'])

            response = json.loads(client.get_secret_value(
                SecretId=str(args_data['secret_id'])
            )['SecretString'])[args_data['env_project']][args_data['env_type']]

            for key, value in response.items():
                data_list.append("{0}={1}\n".format(key, value))

            data_list.append("{0}={1}\n".format(CONTAINER_NAME_KEY, args_data['container_name']))
            data_list.append("{0}={1}\n".format(DOCKER_IMAGE_KEY, args_data['docker_image']))

            self.create_env_file(data_list)

        except ValueError:
            print("Error parsing the arguments, please make sure you are sending the parameters correctly.")
            parser.print_help()

    def create_env_file(self, data_list):

        with open('.env', 'w+') as env_file:
            env_file.writelines(data_list)


parser = argparse.ArgumentParser(prog="python3 secretscript.py", usage='%(prog)s [options]')
parser.add_argument("--access-key-id", help="ACCESS_KEY_ID from AWS to login purposes.", type=str,)
parser.add_argument("--secret-access-key-id", help="SECRET_ACCESS_KEY_ID from AWS to login purposes.", type=str)
parser.add_argument("--region", help="AWS Region which to login.", type=str)
parser.add_argument("--secret-id", help="AWS Secret to retrieve", type=str)
parser.add_argument("--env-project", help="Project name to find in secret data.", type=str)
parser.add_argument("--env-type", help="One of three types: dev, test or prod.", type=str)
parser.add_argument("--docker-image", help="The built docker image.", type=str)
parser.add_argument("--container-name", help="The container name when ran.", type=str)

asr = AwsSecretsRetriever()

asr.get_secret()
