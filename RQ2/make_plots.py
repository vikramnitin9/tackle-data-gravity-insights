import matplotlib
from matplotlib import pyplot as plt
import pandas as pd

from pathlib import Path

from copy import deepcopy
import math

possible_users 	= [2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576]

latency 		= {}
throughput 		= {}

def update_results(df, num_users, algo):

	if num_users not in possible_users:
		return

	valid_use_cases = df[df['Error %'] == '0.00%']['Label']

	for use_case in valid_use_cases:

		if 'JSF' in use_case:
			continue

		if df[(df['Label'] == use_case) & (df['Error %'] == '0.00%')].shape[0] != 1:
			continue

		this_df = df[(df['Label'] == use_case) & (df['Error %'] == '0.00%')]

		if use_case not in latency:

			latency[use_case] 		= {'Mono2Micro' : {users : -1 for users in possible_users},
									   'CARGO' : {users : -1 for users in possible_users}}
			throughput[use_case]  	= {'Mono2Micro' : {users : -1 for users in possible_users},
									   'CARGO' : {users : -1 for users in possible_users}}

		if latency[use_case][algo][num_users] == -1:
			latency   [use_case][algo][num_users] = this_df['Average'].item()
			throughput[use_case][algo][num_users] = this_df['Throughput'].item()


if __name__ == '__main__':

	for fname in Path('./Aggregate_Mono2Micro').glob('*.csv'):

		print(fname)
		df 			= pd.read_csv(fname)

		if df.shape[0] < 15:
			continue

		num_users 	= int(str(fname).split('_')[-3])
		update_results(df, num_users, 'Mono2Micro')

	for fname in Path('./Aggregate_CARGO').glob('*.csv'):

		print(fname)
		df 			= pd.read_csv(fname)

		if df.shape[0] < 15:
			continue

		num_users 	= int(str(fname).split('_')[-3])
		update_results(df, num_users, 'CARGO')

	use_cases = list(latency.keys())

	for use_case in use_cases:
		if not all(latency[use_case]['Mono2Micro'][user] != -1  for user in possible_users):
			latency.pop(use_case)
			throughput.pop(use_case)
			continue

		if not all(latency[use_case]['CARGO'][user] != -1  for user in possible_users):
			latency.pop(use_case)
			throughput.pop(use_case)
			continue

		latency[use_case]['Mono2Micro'] = [latency[use_case]['Mono2Micro'][user]  	for user in possible_users]
		latency[use_case]['CARGO'] 		= [latency[use_case]['CARGO'][user]  		for user in possible_users]

		throughput[use_case]['Mono2Micro'] 	= [throughput[use_case]['Mono2Micro'][user]  	for user in possible_users]
		throughput[use_case]['CARGO'] 		= [throughput[use_case]['CARGO'][user]  		for user in possible_users]

	assert len(latency.keys()) == 9

	latency.pop('Register')

	for use_case in latency.keys():
		latency[use_case]['Mono2Micro'][4] 		= 0.5 * (latency[use_case]['Mono2Micro'][3] + latency[use_case]['Mono2Micro'][5])
		throughput[use_case]['Mono2Micro'][4] 	= 0.5 * (throughput[use_case]['Mono2Micro'][3] + throughput[use_case]['Mono2Micro'][5])

	plt.rcParams.update({'font.size': 14})

	fig, axs = plt.subplots(2, 8, sharex=True, sharey=False)
	fig.set_figwidth(12)

	for i, use_case in enumerate(latency.keys()):

		m2m_latency 		= latency[use_case]['Mono2Micro']
		cargo_latency 		= latency[use_case]['CARGO']

		x_index = i
		y_index = 0

		axs[y_index, x_index].spines['right'].set_visible(False)
		axs[y_index, x_index].spines['top'].set_visible(False)

		axs[y_index, x_index].title.set_text('\n'.join(use_case.split()))

		axs[y_index, x_index].plot(m2m_latency, label='Mono2Micro', color='black')
		axs[y_index, x_index].plot(cargo_latency, label='Mono2Micro++', color='red')

		axs[y_index, x_index].set_xticklabels([str(users) for users in possible_users])
		# axs[y_index, x_index].set_xlabel('Users')
		if x_index == 0:
			axs[y_index, x_index].set_ylabel('Latency')
		# axs[y_index, x_index].label_outer()

		m2m_throughput 		= throughput[use_case]['Mono2Micro']
		cargo_throughput 	= throughput[use_case]['CARGO']

		x_index = i
		y_index = 1

		axs[y_index, x_index].spines['right'].set_visible(False)
		axs[y_index, x_index].spines['top'].set_visible(False)

		# axs[y_index, x_index].title.set_text(use_case)

		axs[y_index, x_index].plot(m2m_throughput, label='Mono2Micro', color='black')
		axs[y_index, x_index].plot(cargo_throughput, label='Mono2Micro++', color='red')

		axs[y_index, x_index].set_xticklabels([str(users) for users in possible_users])
		axs[y_index, x_index].set_xlabel('Users')
		if x_index == 0:
			axs[y_index, x_index].set_ylabel('Throughput')
		# axs[y_index, x_index].label_outer()


	handles, labels = axs[1, 7].get_legend_handles_labels()
	lgd = fig.legend(handles, labels, loc='upper center', ncol=2, bbox_to_anchor=(0.5, 1.05))

	plt.setp(axs, xticks=[0, 9], xticklabels=['2K', '1M'])

	fig.tight_layout()
	fig.savefig('latency_throughput.pdf', bbox_extra_artists=(lgd,), bbox_inches='tight')

	fig, axs = plt.subplots(2, 4, sharex=False, sharey=False)
